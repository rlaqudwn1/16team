# trend_fetcher.py

import time
import requests
import json
from datetime import datetime
from pytrends.request import TrendReq

# 관심 키워드 리스트
keywords = [
    "금리"
]

from pytrends.request import TrendReq

pytrends = TrendReq(
    hl='ko',
    tz=540,
)
results = {}

searches = pytrends.realtime_trending_searches(pn="south_korea")
print(searches)

# 5개씩 나눠서 pytrends 요청
for i in range(0, len(keywords), 5):
    chunk = keywords[i:i+5]
    try:
        pytrends.build_payload(chunk, timeframe='now 1-d')
        df = pytrends.interest_over_time()

        if not df.empty:
            for kw in chunk:
                results[kw] = int(df[kw].iloc[-1])  # 가장 최근 시점 값
                time.sleep(10)
       # 요청 제한 회피를 위한 텀
    except Exception as e:
        print(f"❌ 요청 실패: {e}")
        time.sleep(5)

# 관심도 상위 10개 키워드 추출
top10 = sorted(results.items(), key=lambda x: x[1], reverse=True)[:10]

# 전송 데이터 구조
payload = {
    "collectedAt": datetime.now().isoformat(),
    "trends": [{"keyword": kw, "score": score} for kw, score in top10]
}
print(json.dumps(payload, indent=2, ensure_ascii=False))
# Spring 서버로 POST 요청
try:
    response = requests.post(
        url="http://localhost:8080/api/trends",
        headers={"Content-Type": "application/json"},
        data=json.dumps(payload, ensure_ascii=False)
    )

    print(f"✅ 서버 응답: {response.status_code} - {response.text}")
except Exception as e:
    print(f"❌ 전송 실패: {e}")
