import requests
import json

test_data = [
    {
        "query": "두산 대 한화",
        "searchVolume": 20000,
        "relatedKeywords": ["한화", "임종성", "두산 베어스"],
        "categories": ["Sports"],
        "link": "https://serpapi.com/search.json?q=두산+대+한화"
    },
    {
        "query": "주호민",
        "searchVolume": 5000,
        "relatedKeywords": ["주호민 아들", "무죄", "2심"],
        "categories": ["Law and Government"],
        "link": "https://serpapi.com/search.json?q=주호민"
    }
]

headers = {
    "Content-Type": "application/json; charset=UTF-8"
}

response = requests.post(
    url="http://localhost:8080/api/trends",
    headers=headers,
    data=json.dumps(test_data, ensure_ascii=False).encode("utf-8")
)

print(f"✅ 응답 코드: {response.status_code}")
print(f"✅ 응답 메시지: {response.text}")
