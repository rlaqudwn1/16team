// ✅ script.js (정리된 통합 버전 + 뉴스 필터)
const API_KEY = "65d57137a9164e96aec5ae7d3e9992f0";
let chart = null;
let termIndex = 0;
let termData = [];
let allNews = [];
let newsIndex = 0;
let selectedCategory = 'All';

// 1. 종목 리스트 로딩
function loadStocks() {
  fetch('stocks.json')
    .then(res => res.json())
    .then(stocks => {
      const menu = document.getElementById('stock-menu');
      menu.innerHTML = '';  // 메뉴 초기화

      stocks.forEach(stock => {
        const item = document.createElement('a');
        item.className = 'menu-item';
        item.textContent = stock.name;
        item.href = '#';
        item.dataset.symbol = stock.symbol;

        item.addEventListener('click', (e) => {
          e.preventDefault();
          loadChart(stock.symbol);
          loadQuote(stock.symbol);

          // 클릭된 항목에 'active' 클래스를 추가하여 선택된 상태로 변경
          const menuItems = Array.from(menu.getElementsByClassName('menu-item'));
          menuItems.forEach(item => item.classList.remove('active'));  // 모든 항목에서 'active' 제거
          item.classList.add('active');  // 클릭된 항목에 'active' 추가
        });

        menu.appendChild(item);
      });

      // 첫 번째 종목을 선택된 상태로 표시
      if (stocks.length > 0) {
        const firstItem = menu.querySelector('.menu-item');
        firstItem.classList.add('active');  // 첫 번째 항목에 'active' 클래스 추가
        loadChart(stocks[0].symbol);
        loadQuote(stocks[0].symbol);
      }
    });
}


// 2. 차트 데이터 로딩
async function loadChart(symbol) {
  const url = `https://api.twelvedata.com/time_series?symbol=${symbol}&interval=1day&outputsize=30&apikey=${API_KEY}`;
  const res = await fetch(url);
  const data = await res.json();

  if (!data.values || data.status === 'error') {
    alert('차트 데이터를 불러올 수 없습니다.');
    return;
  }

  const labels = data.values.map(entry => entry.datetime).reverse();
  const prices = data.values.map(entry => parseFloat(entry.close)).reverse();

  // 종목 이름을 차트 위에 표시
  const chartTitle = document.getElementById('chart-title');
  chartTitle.textContent = `주식 차트: ${symbol}`;  // 예: "주식 차트: 두산 대 한화"

  if (chart) chart.destroy();
  chart = new Chart(document.getElementById('stockChart').getContext('2d'), {
    type: 'line',
    data: {
      labels,
      datasets: [{
        data: prices,
        borderColor: 'purple',
        backgroundColor: 'rgba(140,100,255,0.1)',
        fill: true,
        tension: 0.4
      }]
    },
    options: {
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false } },
        y: { beginAtZero: false }
      }
    }
  });
}


// 3. 요약 정보 로딩
async function loadQuote(symbol) {
  const url = `https://api.twelvedata.com/quote?symbol=${symbol}&apikey=${API_KEY}`;
  const res = await fetch(url);
  const quote = await res.json();

  document.getElementById('open').textContent = quote.open || '--';
  document.getElementById('high').textContent = quote.high || '--';
  document.getElementById('low').textContent = quote.low || '--';
  document.getElementById('prevClose').textContent = quote.previous_close || '--';
}




// 4. 경제 용어 카드 슬라이드
function loadDailyTerms() {
  fetch('http://localhost:8080/api/terms/daily')
    .then(res => res.json())
    .then(data => {
      termData = data;
      termIndex = 0;
      showTermCard(termIndex);  // 기존 로직 활용
    });
}
function showTermCard(index) {
  const container = document.getElementById('term-card-container');
  if (!termData[index]) return;
  const term = termData[index];

  container.innerHTML = `
    <div class="term-card">
      <h2>${term.term} <small>(${term.english})</small></h2>
      <p class="definition">${term.definition}</p>
      <p class="example"><strong>예시:</strong> ${term.example}</p>
      <p class="related"><strong>관련 용어:</strong> ${term.related_terms.join(', ')}</p>
    </div>
  `;
}
function searchTerm() {
    const keyword = document.getElementById("searchInput").value;
    if (!keyword) return;

    fetch(`http://localhost:8080/api/terms/search?keyword=${encodeURIComponent(keyword)}`)
        .then(res => res.json())
        .then(data => {
            localStorage.setItem("searchResults", JSON.stringify(data));
            window.location.href = "search.html"; // 결과 페이지로 이동
        })
        .catch(err => {
            console.error("검색 오류:", err);
        });
}


function prevTerm() {
  if (termIndex > 0) {
    termIndex--;
    showTermCard(termIndex);
  }
}

function nextTerm() {
  if (termIndex < termData.length - 1) {
    termIndex++;
    showTermCard(termIndex);
  }
}

// 5. 뉴스 카드 필터 + 출력
async function loadNews() {
  try {
    const response = await fetch('http://localhost:8080/api/news/all'); // ← 절대 경로로 수정
    const data = await response.json();

    console.log('[디버그] 서버에서 받은 뉴스 데이터:', data);

    if (!Array.isArray(data)) {
      console.warn('[경고] 받아온 데이터가 배열이 아닙니다.');
      return;
    }

    allNews = data;
    selectedCategory = 'All';
    renderNewsTabs();
    renderNewsCards();

  } catch (error) {
    console.error('[에러] 뉴스 데이터를 불러오는 중 오류 발생:', error);
  }
}




function renderNewsTabs() {
  const tabs = document.getElementById('news-tabs');

  const categories = ['All', ...new Set(
    allNews.flatMap(n => {
      const time = n.time || '';  // time 필드가 없으면 빈 문자열
      return time.replace('카테고리: ', '').split(', ').filter(cat => cat);
    })
  )];

  tabs.innerHTML = categories.map(cat => `
    <button class="news-tab ${selectedCategory === cat ? 'active' : ''}" onclick="selectCategory('${cat}')">${cat}</button>
  `).join('');
}


function selectCategory(category) {
  selectedCategory = category;
  renderNewsTabs();
  renderNewsCards();
}

function renderNewsCards() {
  const container = document.getElementById('news-cards');
  if (!container) {
    console.warn("❗ 'news-cards' 요소가 존재하지 않습니다.");
    return;
  }

  const filteredNews = selectedCategory === 'All'
    ? allNews
    : allNews.filter(n => n.time?.includes(selectedCategory));

  container.innerHTML = filteredNews.map(n => `
    <div class="news-card">
      <h3 class="news-title">${n.title}</h3>
      <p class="news-summary">${n.gptSummary || n.snippet}</p>
      <a class="news-link" href="${n.link}" target="_blank">기사 보기</a>
    </div>
  `).join('');
}


/// 주식 종목 검색 기능
// 주식 종목 검색 기능
function searchStock() {
  const searchTerm = document.getElementById('stock-search').value.trim().toLowerCase();
  
  if (!searchTerm) {
    alert('주식 종목을 입력하세요.');
    return;
  }

  fetch('stocks.json')  // 종목 리스트 파일을 가져오기
    .then(res => res.json())
    .then(stocks => {
      const menu = document.getElementById('stock-menu');
      const menuItems = Array.from(menu.getElementsByClassName('menu-item'));

      // 검색된 종목만 필터링하여 표시
      const filteredStocks = stocks.filter(stock => stock.name.toLowerCase().includes(searchTerm));

      // 기존 메뉴 항목에 'active' 클래스를 추가하지 않도록 변경
      menuItems.forEach(item => {
        item.classList.remove('active');  // 이전에 선택된 항목에서 'active' 클래스 제거
      });

      // 검색된 종목을 메뉴에 추가하기
      filteredStocks.forEach(stock => {
        const existingItem = menu.querySelector(`[data-symbol="${stock.symbol}"]`);
        if (!existingItem) {  // 해당 종목이 없다면 추가
          const item = document.createElement('a');
          item.className = 'menu-item';
          item.textContent = stock.name;
          item.href = '#';
          item.dataset.symbol = stock.symbol;

          item.addEventListener('click', (e) => {
            e.preventDefault();
            loadChart(stock.symbol);  // 해당 종목의 차트 로딩
            loadQuote(stock.symbol);  // 해당 종목의 요약 정보 로딩

            // 클릭된 항목에 'active' 클래스를 추가하여 선택된 상태로 변경
            menuItems.forEach(item => item.classList.remove('active'));  // 모든 항목에서 'active' 클래스 제거
            item.classList.add('active');  // 클릭된 항목에 'active' 클래스 추가
          });

          menu.appendChild(item);  // 검색된 결과를 메뉴에 추가
        }
      });

      if (filteredStocks.length > 0) {
        // 첫 번째 검색 결과의 차트와 요약 정보 로드
        loadChart(filteredStocks[0].symbol);
        loadQuote(filteredStocks[0].symbol);
      } else {
        alert('검색된 종목이 없습니다.');
      }
    })
    .catch(error => {
      console.error('검색 중 오류 발생:', error);
      alert('검색에 실패했습니다.');
    });
}

// ✅ 환율 카드 로딩 (USD/KRW)
async function loadExchangeRate() {
  try {
    const url = `https://api.twelvedata.com/quote?symbol=USD/KRW&apikey=65d57137a9164e96aec5ae7d3e9992f0`;
    const res = await fetch(url);
    const data = await res.json();

    const container = document.getElementById('exchange-card-container');
    container.innerHTML = `
      <div style="
        background: rgba(255, 255, 255, 0.85);
        padding: 24px;
        border-radius: 16px;
        box-shadow: 0 6px 20px rgba(0,0,0,0.07);
        max-width: 380px;
        text-align: center;
      ">
        <h3>🇺🇸 USD ➜ 🇰🇷 KRW</h3>
        <p><strong>현재가:</strong> ${data.price || '--'} 원</p>
        <p><strong>고가:</strong> ${data.high || '--'} 원</p>
        <p><strong>저가:</strong> ${data.low || '--'} 원</p>
      </div>
    `;
  } catch (err) {
    console.error("환율 데이터를 불러오는 데 실패:", err);
    document.getElementById('exchange-card-container').textContent = "불러오기 실패";
  }
}
// 💱 여러 환율 카드 슬라이더
const exchangeList = [
  { symbol: "USD/KRW", label: "🇺🇸 USD ➜ 🇰🇷 KRW" },
  { symbol: "USD/JPY", label: "🇺🇸 USD ➜ 🇯🇵 JPY" },
  { symbol: "USD/EUR", label: "🇺🇸 USD ➜ 🇪🇺 EUR" },
  { symbol: "USD/GBP", label: "🇺🇸 USD ➜ 🇬🇧 GBP" },
  { symbol: "USD/CNY", label: "🇺🇸 USD ➜ 🇨🇳 CNY" }
];
let exchangeIndex = 0;

async function loadExchangeRate(index = 0) {
  const { symbol, label } = exchangeList[index];
  const url = `https://api.twelvedata.com/quote?symbol=${symbol}&apikey=${API_KEY}`;
  try {
    const res = await fetch(url);
    const data = await res.json();

    const container = document.getElementById('exchange-card-container');
    container.innerHTML = `
      <div style="
        background: rgba(255, 255, 255, 0.85);
        padding: 24px;
        border-radius: 16px;
        box-shadow: 0 6px 20px rgba(0,0,0,0.07);
        max-width: 380px;
        text-align: center;
      ">
        <h3>${label}</h3>
        <p><strong>현재가:</strong> ${data.price || '--'} 원</p>
        <p><strong>고가:</strong> ${data.high || '--'} 원</p>
        <p><strong>저가:</strong> ${data.low || '--'} 원</p>
      </div>
    `;
  } catch (err) {
    console.error("환율 로딩 실패:", err);
    document.getElementById('exchange-card-container').textContent = "불러오기 실패";
  }
}

function nextExchange() {
  exchangeIndex = (exchangeIndex + 1) % exchangeList.length;
  loadExchangeRate(exchangeIndex);
}

function prevExchange() {
  exchangeIndex = (exchangeIndex - 1 + exchangeList.length) % exchangeList.length;
  loadExchangeRate(exchangeIndex);
}
//더미 데이터들 꼭 지울 것!!!!
function loadDailyTerms() {
  // ✅ 백엔드 없이도 동작하는 더미 데이터
  termData = [
    {
      term: "GDP",
      english: "Gross Domestic Product",
      definition: "한 나라에서 일정 기간 동안 생산된 모든 재화와 서비스의 시장 가치",
      example: "GDP가 증가하면 생산과 고용이 늘어나고, 기업 이익 증가로 주식시장도 긍정적인 영향을 받을 수 있다.",
      related_terms: ["GNP", "성장률", "국민소득"]
    },
    {
      term: "인플레이션",
      english: "Inflation",
      definition: "전반적인 상품과 서비스 가격이 지속적으로 상승하는 현상",
      example: "인플레이션이 심해지면 중앙은행은 기준금리를 인상한다.",
      related_terms: ["디플레이션", "물가상승률", "기준금리"]
    }
  ];
  termIndex = 0;
  showTermCard(termIndex);
}
function loadNews() {
  // ✅ 백엔드 없이도 동작하는 더미 뉴스 데이터
  allNews = [
    {
      title: "한국 증시 상승 마감",
      gptSummary: "코스피와 코스닥 지수 모두 상승 마감했으며, 반도체주 중심의 강세가 두드러졌다.",
      time: "카테고리: 경제",
      link: "https://example.com/news1"
    },
    {
      title: "달러 환율 1300원 돌파",
      gptSummary: "원달러 환율이 1300원을 돌파하며 수입물가 상승 우려가 커지고 있다.",
      time: "카테고리: 금융",
      link: "https://example.com/news2"
    }
  ];
  selectedCategory = 'All';
  renderNewsTabs();
  renderNewsCards();
}



// 6. 초기 실행

document.addEventListener('DOMContentLoaded', () => {
  loadStocks();
  loadDailyTerms();
  loadNews();
  loadExchangeRate(); 
});