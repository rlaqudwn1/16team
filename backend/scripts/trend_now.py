import requests
import json
from serpapi import GoogleSearch
from dotenv import load_dotenv
import os

load_dotenv()
api_key = os.getenv("SERPAPI_API_KEY")

params = {
    "engine": "google_trends_trending_now",
    "geo": "KR",
    "hl": "ko",
    "tz": "-540",
    "api_key": api_key
}

search = GoogleSearch(params)
results = search.get_dict()

# 데이터 정제
trending_data = []
for trend in results.get("trending_searches", []):
    trending_data.append({
        "query": trend.get("query"),
        "searchVolume": trend.get("search_volume"),
        "relatedKeywords": trend.get("trend_breakdown"),
        "categories": [c["name"] for c in trend.get("categories", [])],
        "link": trend.get("serpapi_google_trends_link")
    })

# POST 요청
response = requests.post(
    url="http://localhost:8080/api/trends",  # Spring API 엔드포인트
    headers={"Content-Type": "application/json"},
    data=json.dumps(trending_data, ensure_ascii=False)
)

print(f"✅ 서버 응답: {response.status_code} - {response.text}")
