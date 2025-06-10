import os
import re
import json
import requests
from urllib.parse import urlparse
from bs4 import BeautifulSoup
from datetime import datetime, timedelta
from dateutil import parser
from serpapi import GoogleSearch
from dotenv import load_dotenv

def load_keywords_from_json(json_path: str) -> list:
    with open(json_path, "r", encoding="utf-8") as f:
        keyword_dict = json.load(f)

    # 모든 키워드 평탄화(flatten)해서 리스트로 반환
    all_keywords = sum(keyword_dict.values(), [])
    return all_keywords


# ✅ SerpApi API 키 로딩
load_dotenv()
api_key = os.getenv("SERPAPI_API_KEY")

# 🔍 키워드 기반 뉴스 수집 (구글 뉴스 + 신뢰 도메인 제한)
keywords = load_keywords_from_json("keywords.json")
print("총 키워드 개수:", len(keywords))
trusted_sites = ["site:yna.co.kr", "site:hankyung.com"]
query = f"({' OR '.join(keywords)}) ({' OR '.join(trusted_sites)})"

params = {
    "engine": "google_news",
    "q": query,
    "hl": "ko",
    "gl": "KR",
    "api_key": api_key
}

search = GoogleSearch(params)
results = search.get_dict()

# 📌 본문 추출 셀렉터
DOMAIN_SELECTOR_MAP = {
    "yna.co.kr": ["div#articleWrap", "div#articleViewArea", "div.story-news.article"],
    "hankyung.com": ["div#articletxt", "div#newsview", "article"]
}
fallback_selectors = ["div.article", "article", "div.content", "div.text"]

# ⚙️ 유틸리티 함수들
def get_domain_key(url):
    domain = urlparse(url).netloc.lower()
    return domain[4:] if domain.startswith("www.") else domain

def is_blocked_domain(url):
    return any(block in url for block in ["cb.yna.co.kr", "plus.hankyung.com", "premium.naver.com"])

def is_valid_article(text):
    if not text or len(text.strip()) < 100:
        return False
    blocked_words = ["공지사항", "유료서비스", "회사소개", "이용약관"]
    return not any(bad in text for bad in blocked_words)

def clean_text(text):
    text = re.sub(r"[가-힣]+\s기자\s*\([^)]+\)", "", text)
    text = re.sub(r"입력\s*[：:]\s*\d{4}\.\d{2}\.\d{2}.*", "", text)
    for kw in ["기자", "구독", "구독중", "이전", "다음", "이미지 확대",
               "공유", "프린트", "트위터", "페이스북", "카카오톡", "댓글", "클린뷰"]:
        text = text.replace(kw, "")
    text = re.sub(r"\n{2,}", "\n", text)
    text = re.sub(r"[ \t]{2,}", " ", text)
    return text.strip()

def extract_article_text(soup, selectors):
    max_len = 0
    best = None
    for sel in selectors:
        tag = soup.select_one(sel)
        if tag:
            text = tag.get_text(separator="\n", strip=True)
            if is_valid_article(text) and len(text) > max_len:
                best = text
                max_len = len(text)
    return clean_text(best) if best else None

def fetch_article_body(url):
    if is_blocked_domain(url):
        return None
    try:
        headers = {
            "User-Agent": "Mozilla/5.0",
            "Referer": "https://www.google.com/",
            "Accept-Language": "ko-KR,ko;q=0.9"
        }
        resp = requests.get(url, headers=headers, timeout=6)
        soup = BeautifulSoup(resp.text, "html.parser")
        domain = get_domain_key(url)
        selectors = DOMAIN_SELECTOR_MAP.get(domain, fallback_selectors)
        return extract_article_text(soup, selectors)
    except Exception as e:
        print(f"⚠️ 크롤링 실패: {url} → {e}")
        return None

def normalize_date(date_str):
    try:
        if "시간 전" in date_str:
            hours = int(re.search(r"\d+", date_str).group())
            return (datetime.now() - timedelta(hours=hours)).isoformat()
        elif "일 전" in date_str:
            days = int(re.search(r"\d+", date_str).group())
            return (datetime.now() - timedelta(days=days)).isoformat()
        else:
            return parser.parse(date_str).isoformat()
    except:
        return None

# 📦 뉴스 평탄화 및 전송 준비
payload = []
articles = results.get("news_results", [])[:5]

for a in articles:
    title = a.get("title")
    link = a.get("link")
    snippet = a.get("snippet")
    source = a.get("source", {}).get("name", "")
    published_at = normalize_date(a.get("date", ""))
    content = fetch_article_body(link)
    if not content:
        continue  # ❗ content 없는 기사 제외

    news = {
        "title": title,
        "link": link,
        "source": source,
        "snippet": snippet,
        "publishedAt": published_at,
        "content": content,
        "gptSummary": None,
        "thumbnail": ""
    }
    payload.append(news)

# 🚀 Spring 서버로 POST 전송
try:
    res = requests.post(
        url="http://localhost:8080/api/news",
        headers={"Content-Type": "application/json"},
        data=json.dumps(payload, ensure_ascii=False)
    )
    if res.status_code == 200:
        print("✅ 서버 전송 성공")
    else:
        print(f"❌ 서버 오류: {res.status_code} - {res.text}")
except Exception as e:
    print(f"🚨 전송 실패: {e}")
