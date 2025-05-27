import requests
import json
import os
import re
from serpapi import GoogleSearch
from dotenv import load_dotenv

# .env에서 API 키 로딩
load_dotenv()
api_key = os.getenv("SERPAPI_API_KEY")

# SerpApi 파라미터 설정
params = {
    "engine": "google_trends_trending_now",
    "geo": "KR",
    "hl": "ko",
    "tz": "-540",
    "api_key": api_key
}

# SerpApi 요청
search = GoogleSearch(params)
results = search.get_dict()

# 유효 키워드 판별 함수
def is_valid_keyword(k):
    return bool(re.match(r"^[가-힣a-zA-Z0-9\s\(\)]+$", k))

# 트렌드 리스트 생성
trend_payload = []

for trend in results.get("trending_searches", []):
    keyword = trend.get("query", "").strip()
    related = trend.get("trend_breakdown", []) or []

    # keyword + related 합치고 유효성 필터링
    all_keywords = list({k.strip() for k in [keyword] + related if is_valid_keyword(k)})

    # 단일 카테고리 선택
    category_list = trend.get("categories", [])
    category = category_list[0]["name"] if category_list else "Uncategorized"

    if all_keywords:
        trend_payload.append({
            "category": category,
            "keywords": all_keywords
        })

# ✅ 테스트용 샘플 추가 (선택)
trend_payload.append({
    "category": "Technology",
    "keywords": ["인공지능", "AI", "클라우드", "반도체", "NVIDIA", "삼성전자"]
})

# ✅ 출력
print(f"✅ 수집된 트렌드 항목 수: {len(trend_payload)}")
for item in trend_payload:
    print(f"📌 [{item['category']}] → {item['keywords']}")

# ✅ Spring 서버로 전송
try:
    response = requests.post(
        url="http://localhost:8080/api/trends",
        headers={"Content-Type": "application/json"},
        data=json.dumps(trend_payload, ensure_ascii=False)
    )

    if response.status_code == 200:
        print("✅ 트렌드 데이터 성공적으로 전송됨!")
    else:
        print(f"❌ 서버 응답 오류: {response.status_code} - {response.text}")

except Exception as e:
    print(f"🚨 요청 중 예외 발생: {e}")
