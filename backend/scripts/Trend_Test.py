import requests
import json
from serpapi import GoogleSearch
from dotenv import load_dotenv
import os
import re

load_dotenv()
api_key = os.getenv("SERPAPI_API_KEY")

# SerpApi 파라미터 설정
params = {
    "engine": "google_trends_trending_now",
    "geo": "KR",
    "hl": "ko",
    "tz": "-540",
    "api_key": api_key,
    "cat" : 18
}

# SerpApi 요청
search = GoogleSearch(params)
results = search.get_dict()

# 키워드 수집 및 정제
raw_keywords = [
    trend.get("query", "").strip()
    for trend in results.get("trending_searches", [])
]

# 중복 제거 + 한글/숫자/영어만 포함된 키워드만 필터링
def is_valid_keyword(k):
    return bool(re.match(r"^[가-힣a-zA-Z0-9\s\(\)]+$", k))

clean_keywords = list({kw for kw in raw_keywords if is_valid_keyword(kw)})

print(f"✅ 실시간 수집 키워드 ({len(clean_keywords)}개): {clean_keywords}")

# Spring 서버로 추천 요청
payload = {
    "keywords": clean_keywords
}

try:
    response = requests.post(
        url="http://localhost:8080/api/recommendations",
        headers={"Content-Type": "application/json"},
        data=json.dumps(payload, ensure_ascii=False)
    )

    if response.status_code == 200:
        recommendations = response.json()
        print("📊 추천 결과:")
        for rec in recommendations:
            print(f"🔎 {rec['keyword']} → {', '.join(rec['matchedCompanies'])}")
    else:
        print(f"❌ 서버 응답 오류: {response.status_code} - {response.text}")
except Exception as e:
    print(f"🚨 요청 중 오류 발생: {e}")
