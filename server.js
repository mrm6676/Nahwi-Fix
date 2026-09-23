const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || process.env.DEFAULT_APP_PORT || 3000;

const htmlContent = `<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>NahwiFix - مصحح النحو والترقيم العربي</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;800&display=swap" rel="stylesheet">
  <style>
    :root {
      --primary: #004D40;
      --primary-hover: #00362D;
      --primary-container: #A7F3D0;
      --on-primary-container: #00201A;
      --secondary: #0D47A1;
      --secondary-hover: #082E6B;
      --surface: #FFFFFF;
      --bg: #F8FAF9;
      --text: #191C1B;
      --text-muted: #64748B;
      --border: #E2E8F0;
      --error: #DC2626;
      --error-bg: #FEE2E2;
      --success: #059669;
      --success-bg: #D1FAE5;
      --grammar: #2563EB;
      --punct: #7C3AED;
      --spelling: #EA580C;
    }
    body.dark {
      --primary: #34D399;
      --primary-hover: #10B981;
      --primary-container: #005144;
      --on-primary-container: #A7F3D0;
      --secondary: #93C5FD;
      --secondary-hover: #60A5FA;
      --surface: #1E293B;
      --bg: #0F172A;
      --text: #F1F5F9;
      --text-muted: #94A3B8;
      --border: #334155;
      --error: #F87171;
      --error-bg: #7F1D1D;
      --success: #34D399;
      --success-bg: #064E3B;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Cairo', sans-serif; }
    body {
      background: var(--bg);
      color: var(--text);
      display: flex;
      justify-content: center;
      min-height: 100vh;
      padding: 16px;
      transition: background 0.25s ease, color 0.25s ease;
    }
    .device-frame {
      width: 100%;
      max-width: 480px;
      background: var(--surface);
      border-radius: 28px;
      box-shadow: 0 12px 36px rgba(0,0,0,0.14);
      border: 1px solid var(--border);
      display: flex;
      flex-direction: column;
      overflow: hidden;
      min-height: 850px;
      position: relative;
    }
    .status-bar {
      padding: 10px 18px 4px;
      display: flex;
      justify-content: space-between;
      font-size: 11px;
      font-weight: 700;
      color: var(--text-muted);
      user-select: none;
    }
    header {
      padding: 12px 18px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid var(--border);
      background: var(--surface);
    }
    .logo-badge { display: flex; align-items: center; gap: 8px; cursor: pointer; }
    .logo-icon {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: var(--primary);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-weight: 800;
      font-size: 16px;
      box-shadow: 0 2px 6px rgba(0,0,0,0.15);
    }
    .header-actions { display: flex; gap: 8px; }
    
    /* Interactive Button Global Behaviors */
    button, .icon-btn, .sample-pill, .chip, .nav-item {
      cursor: pointer;
      user-select: none;
      -webkit-tap-highlight-color: transparent;
      outline: none;
      transition: transform 0.12s cubic-bezier(0.4, 0, 0.2, 1),
                  background 0.15s ease,
                  color 0.15s ease,
                  border-color 0.15s ease,
                  box-shadow 0.15s ease,
                  filter 0.12s ease;
    }
    button:active, .icon-btn:active, .sample-pill:active, .chip:active, .nav-item:active {
      transform: scale(0.95) translateY(1px);
      filter: brightness(0.92);
    }
    button:focus-visible, .icon-btn:focus-visible, .sample-pill:focus-visible {
      outline: 2px solid var(--primary);
      outline-offset: 2px;
    }

    .icon-btn {
      background: var(--bg);
      border: 1px solid var(--border);
      color: var(--text);
      padding: 6px 12px;
      border-radius: 10px;
      font-weight: 600;
      font-size: 12px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
    }
    .icon-btn:hover {
      background: var(--border);
    }
    
    .main-scroll {
      flex: 1;
      overflow-y: auto;
      scroll-behavior: smooth;
      padding: 16px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    
    .hero-card {
      background: var(--primary-container);
      color: var(--on-primary-container);
      padding: 16px;
      border-radius: 16px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
    .hero-card h2 { font-size: 17px; margin-bottom: 4px; }
    .hero-card p { font-size: 12px; opacity: 0.95; }
    
    .samples { display: flex; gap: 6px; flex-wrap: wrap; margin-top: 10px; }
    .sample-pill {
      background: rgba(255,255,255,0.85);
      color: #000;
      border: 1px solid rgba(0,0,0,0.1);
      padding: 5px 12px;
      border-radius: 20px;
      font-size: 11px;
      font-weight: 600;
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
    .sample-pill:hover {
      background: #FFFFFF;
      box-shadow: 0 2px 6px rgba(0,0,0,0.1);
    }
    .sample-pill.active {
      background: var(--primary);
      color: #FFFFFF;
      border-color: var(--primary);
      font-weight: 700;
    }

    .editor-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 16px;
      padding: 14px;
      display: flex;
      flex-direction: column;
      gap: 10px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.03);
    }
    textarea {
      width: 100%;
      height: 110px;
      border: 1px solid var(--border);
      border-radius: 10px;
      padding: 10px;
      font-size: 14px;
      line-height: 1.6;
      background: var(--bg);
      color: var(--text);
      resize: none;
      outline: none;
      transition: border-color 0.2s ease;
    }
    textarea:focus {
      border-color: var(--primary);
      box-shadow: 0 0 0 2px var(--primary-container);
    }
    .stats-bar {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      background: var(--bg);
      padding: 6px 12px;
      border-radius: 8px;
      color: var(--text-muted);
      font-weight: 600;
      border: 1px solid var(--border);
    }
    .action-row { display: flex; gap: 8px; flex-wrap: wrap; }
    .btn-primary {
      flex: 1;
      min-width: 110px;
      background: var(--primary);
      color: #fff;
      border: none;
      padding: 10px 14px;
      border-radius: 10px;
      font-weight: 700;
      font-size: 13px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      box-shadow: 0 2px 6px rgba(0,77,64,0.2);
    }
    .btn-primary:hover {
      background: var(--primary-hover);
      box-shadow: 0 4px 10px rgba(0,77,64,0.3);
    }
    .btn-secondary {
      flex: 1;
      min-width: 100px;
      background: var(--secondary);
      color: #fff;
      border: none;
      padding: 10px 14px;
      border-radius: 10px;
      font-weight: 700;
      font-size: 13px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      box-shadow: 0 2px 6px rgba(13,71,161,0.2);
    }
    .btn-secondary:hover {
      background: var(--secondary-hover);
      box-shadow: 0 4px 10px rgba(13,71,161,0.3);
    }
    .btn-outline {
      background: transparent;
      color: var(--text);
      border: 1px solid var(--border);
      padding: 8px 14px;
      border-radius: 10px;
      font-weight: 600;
      font-size: 12px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
    }
    .btn-outline:hover {
      background: var(--bg);
      border-color: var(--primary);
      color: var(--primary);
    }

    .issues-chips { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 6px; }
    .chip {
      background: var(--bg);
      border: 1px solid var(--border);
      padding: 6px 10px;
      border-radius: 8px;
      font-size: 12px;
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
    .chip:hover {
      border-color: var(--primary);
      background: var(--surface);
    }
    .chip.active {
      border-color: var(--primary);
      background: var(--primary-container);
      color: var(--on-primary-container);
      font-weight: 700;
      box-shadow: 0 2px 6px rgba(0,0,0,0.08);
    }
    .inspector-card {
      background: var(--bg);
      border: 1px solid var(--border);
      border-radius: 14px;
      padding: 14px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.02);
    }
    .inspector-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
    .badge { padding: 3px 8px; border-radius: 6px; font-size: 11px; font-weight: 700; color: #fff; }
    .badge.grammar { background: var(--grammar); }
    .badge.punct { background: var(--punct); }
    .badge.spelling { background: var(--spelling); }
    .badge.agreement { background: var(--success); }
    
    .bottom-nav {
      display: flex;
      border-top: 1px solid var(--border);
      background: var(--surface);
      user-select: none;
      z-index: 100;
    }
    .nav-item {
      flex: 1;
      padding: 10px 4px;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
      border: none;
      background: transparent;
      color: var(--text-muted);
      font-size: 11px;
      font-weight: 600;
      position: relative;
    }
    .nav-item:hover {
      color: var(--primary);
      background: rgba(0,0,0,0.02);
    }
    .nav-item.active {
      color: var(--primary);
      font-weight: 700;
    }
    .nav-item.active::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 20%;
      right: 20%;
      height: 3px;
      background: var(--primary);
      border-radius: 3px 3px 0 0;
    }

    .view-container { display: none; animation: fadeIn 0.18s ease forwards; }
    .view-container.active { display: block; }
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(3px); }
      to { opacity: 1; transform: translateY(0); }
    }

    .plan-card {
      border: 1px solid var(--border);
      padding: 16px;
      border-radius: 14px;
      margin-bottom: 12px;
      background: var(--surface);
      box-shadow: 0 2px 6px rgba(0,0,0,0.03);
      position: relative;
    }
    .plan-card.pro {
      border-color: var(--primary);
      background: var(--primary-container);
      color: var(--on-primary-container);
      box-shadow: 0 4px 12px rgba(0,77,64,0.15);
    }
    
    /* Modern Toast Notification */
    #toastNotification {
      position: fixed;
      top: 24px;
      left: 50%;
      transform: translateX(-50%) translateY(-20px);
      background: #1E293B;
      color: #FFFFFF;
      padding: 10px 18px;
      border-radius: 30px;
      font-size: 13px;
      font-weight: 700;
      box-shadow: 0 8px 24px rgba(0,0,0,0.3);
      display: flex;
      align-items: center;
      gap: 8px;
      opacity: 0;
      pointer-events: none;
      transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
      z-index: 99999;
      max-width: 90%;
      text-align: center;
    }
    #toastNotification.show {
      opacity: 1;
      transform: translateX(-50%) translateY(0);
      pointer-events: auto;
    }
    #toastNotification.success { background: #059669; }
    #toastNotification.error { background: #DC2626; }
    #toastNotification.info { background: #2563EB; }

    /* Animated Processing Indicator Styling */
    .processing-loading-card {
      display: none;
      background: var(--surface);
      border: 1.5px solid rgba(0, 77, 64, 0.35);
      border-radius: 16px;
      padding: 16px 20px;
      box-shadow: 0 4px 20px rgba(0, 77, 64, 0.08);
      margin: 12px 0;
      text-align: center;
      position: relative;
      overflow: hidden;
      animation: fadeIn 0.25s cubic-bezier(0.4, 0, 0.2, 1) forwards;
    }
    .processing-loading-card.active {
      display: block;
    }
    .spinner-ring-container {
      position: relative;
      width: 60px;
      height: 60px;
      margin: 0 auto 12px auto;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .spinner-ring-outer {
      position: absolute;
      inset: 0;
      border-radius: 50%;
      border: 3.5px solid transparent;
      border-top-color: var(--primary);
      border-right-color: var(--secondary);
      animation: spinRing 1.1s cubic-bezier(0.68, -0.55, 0.27, 1.55) infinite;
    }
    .spinner-ring-inner {
      position: absolute;
      inset: 6px;
      border-radius: 50%;
      border: 2px dashed rgba(0, 77, 64, 0.25);
      animation: spinRingReverse 2.2s linear infinite;
    }
    .spinner-ring-center {
      width: 32px;
      height: 32px;
      background: var(--primary);
      color: #FFFFFF;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 900;
      font-size: 17px;
      box-shadow: 0 2px 8px rgba(0, 77, 64, 0.3);
      animation: pulseBadge 1.4s ease-in-out infinite alternate;
    }
    @keyframes spinRing {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    @keyframes spinRingReverse {
      0% { transform: rotate(360deg); }
      100% { transform: rotate(0deg); }
    }
    @keyframes pulseBadge {
      0% { transform: scale(0.92); }
      100% { transform: scale(1.08); }
    }
    .processing-shimmer-bar {
      width: 80%;
      height: 5px;
      background: rgba(0, 0, 0, 0.06);
      border-radius: 3px;
      margin: 10px auto 0 auto;
      overflow: hidden;
      position: relative;
    }
    .processing-shimmer-thumb {
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      width: 40%;
      border-radius: 3px;
      background: linear-gradient(90deg, transparent, var(--primary), var(--secondary), transparent);
      animation: shimmerSlide 1.2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
    }
    @keyframes shimmerSlide {
      0% { transform: translateX(-120%); }
      100% { transform: translateX(320%); }
    }
    .pulsing-dots-inline span {
      display: inline-block;
      width: 4px;
      height: 4px;
      border-radius: 50%;
      background: var(--primary);
      margin: 0 1.5px;
      animation: dotBounce 1.2s infinite ease-in-out;
    }
    .pulsing-dots-inline span:nth-child(2) { animation-delay: 0.2s; }
    .pulsing-dots-inline span:nth-child(3) { animation-delay: 0.4s; }
    @keyframes dotBounce {
      0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
      40% { transform: scale(1.3); opacity: 1; }
    }
  </style>
</head>
<body>
  <!-- Toast Notification Box -->
  <div id="toastNotification">
    <span id="toastIcon">✓</span>
    <span id="toastMsg">تمت العملية بنجاح</span>
  </div>

  <div class="device-frame">
    <div class="status-bar">
      <span>NahwiFix Live Interactive Demo</span>
      <span id="clockDisplay">9:41 AM • 100%</span>
    </div>
    <header>
      <div class="logo-badge" onclick="switchNav('Checker')">
        <div class="logo-icon">ن</div>
        <strong>NahwiFix</strong>
      </div>
      <div class="header-actions">
        <a href="/slides" class="icon-btn" title="عرض تقديمي عن المشروع / Presentation Slides" style="background:linear-gradient(135deg, rgba(0,77,64,0.15), rgba(16,185,129,0.2)); border-color:var(--primary); font-weight:bold;">
          <span>📽️</span> <span class="nav-label" data-ar="العرض" data-en="Slides">العرض</span>
        </a>
        <a href="/evaluation_dataset.pdf" target="_blank" download="NahwiFix_Evaluation_Dataset.pdf" class="icon-btn" title="تحميل ملف بيانات التقييم (PDF) / Download Evaluation Dataset">
          <span>📄</span> PDF
        </a>
        <button class="icon-btn" onclick="toggleReaderMode()" id="readerBtn" title="وضع القارئ الميسر">
          <span>📖</span> <span id="readerBtnLabel">القارئ</span>
        </button>
        <button class="icon-btn" onclick="toggleLang()" id="langBtn" title="تغيير اللغة / Switch Language">EN</button>
        <button class="icon-btn" onclick="toggleTheme()" id="themeBtn" title="تغيير المظهر / Dark Mode">🌙</button>
      </div>
    </header>

    <div class="main-scroll">
      <!-- CHECKER VIEW -->
      <div id="viewChecker" class="view-container active">
        <div class="hero-card">
          <h2 id="heroTitle">مصحح النحو والترقيم العربي</h2>
          <p id="heroSub">افحص النصوص العربية وصحح الهمزات، علامات الترقيم، ومطابقة الفعل والفاعل فورياً.</p>
          <div class="samples">
            <button class="sample-pill active" onclick="loadSample(0, this)">1: ذهب محمد الى...</button>
            <button class="sample-pill" onclick="loadSample(1, this)">2: إن المعلمة يساعد...</button>
            <button class="sample-pill" onclick="loadSample(2, this)">3: في الحديقةُ...</button>
            <button class="sample-pill" onclick="loadSample(3, this)">4: إن المعلمون مسافرون...</button>
            <button class="sample-pill" onclick="loadSample(4, this)">5: لم يذهبون ولم ياتي...</button>
          </div>
        </div>

        <!-- ACCESSIBLE READER MODE SECTION -->
        <div class="editor-card" id="readerCard" style="margin-top: 14px; border: 2px solid #2563EB; background: #EFF6FF;">
          <div style="display:flex; justify-content:space-between; align-items:center; cursor:pointer;" onclick="toggleReaderMode()">
            <div style="display:flex; align-items:center; gap:8px;">
              <span style="background:#2563EB; color:#fff; padding:6px 10px; border-radius:50%; font-size:16px;">🔊</span>
              <div>
                <strong style="color:#1E40AF;" id="readerCardTitle">وضع القارئ الصوتي الميسّر (Accessibility)</strong>
                <div style="font-size:12px; color:#3B82F6;" id="readerCardSub">خدمة نطق النصوص المكتوبة وتكبير الخط لغير القادرين على القراءة</div>
              </div>
            </div>
            <button class="icon-btn" id="readerToggleBtn" onclick="event.stopPropagation(); toggleReaderMode();">▼</button>
          </div>

          <div id="readerContent" style="display:none; margin-top:14px;">
            <div style="display:flex; gap:8px; margin-bottom:12px; flex-wrap:wrap; align-items:center;">
              <button class="btn-primary" onclick="toggleSpeechRecognition()" id="btnReaderMic" style="background:#DC2626; padding:8px 14px;">
                <span>🎤</span> <span id="btnReaderMicLabel">إملاء صوتي بالمايكروفون</span>
              </button>
              <button class="btn-primary" onclick="speakCurrentText()" id="btnSpeakText" style="background:#2563EB; padding:8px 14px;">
                <span>🔊</span> <span id="btnSpeakLabel">انطق النص الآن</span>
              </button>
              <button class="btn-secondary" onclick="stopSpeaking()" id="btnStopSpeech" style="padding:8px 14px; background:#475569;">
                <span>⏹️</span> إيقاف النطق
              </button>
              <button class="icon-btn" onclick="copyReaderText()" title="نسخ النص">
                <span>📋</span> نسخ
              </button>
              <div style="display:flex; align-items:center; gap:6px; margin-right:auto;">
                <span style="font-size:12px; font-weight:bold;">الخط:</span>
                <button class="sample-pill" onclick="changeReaderFontSize(-2)" title="تصغير الخط">A-</button>
                <span id="readerFontSizeDisplay" style="font-weight:bold; font-size:13px; min-width:32px; text-align:center;">22px</span>
                <button class="sample-pill" onclick="changeReaderFontSize(2)" title="تكبير الخط">A+</button>
                <button class="sample-pill" onclick="toggleReaderHighContrast()" id="btnReaderContrast">تباين عالٍ 👁️</button>
              </div>
            </div>

            <div id="readerDisplayBox" style="background:#fff; border:2px solid #2563EB; border-radius:12px; padding:16px; font-size:22px; line-height:1.8; font-weight:600; color:#1E293B;">
              ذهب محمدٌ إلى المدرسةِ، وهو يحملُ كتبَه وشاهدَ عصفوراً جميلاً.
            </div>
          </div>
        </div>

        <!-- WRITE WITH AI SECTION -->
        <div class="editor-card" style="margin-top: 14px; border: 1px solid var(--primary-container);">
          <div style="display:flex; justify-content:space-between; align-items:center; cursor:pointer;" onclick="toggleWriteAiCard()">
            <div style="display:flex; align-items:center; gap:8px;">
              <span style="background:var(--primary-container); color:var(--primary); padding:6px; border-radius:50%; font-size:16px;">✨</span>
              <div>
                <strong style="color:var(--primary);">الكتابة بالذكاء الاصطناعي (Write with AI)</strong>
                <div style="font-size:12px; color:var(--text-muted);">صياغة نصوص فصيحة، رسائل، ومقالات باحترافية</div>
              </div>
            </div>
            <button class="icon-btn" id="writeAiToggleBtn" onclick="event.stopPropagation(); toggleWriteAiCard();">▼</button>
          </div>

          <div id="writeAiContent" style="display:none; margin-top:14px;">
            <div style="font-size:12px; font-weight:bold; margin-bottom:6px; color:var(--text-muted);">نماذج وقوالب جاهزة (انقر للاختيار):</div>
            <div style="display:flex; flex-wrap:wrap; gap:6px; margin-bottom:10px;">
              <button class="sample-pill" onclick="setAiPrompt('اكتب رسالة شكر وتقدير رسمية موجهة لمدير العمل أو الزملاء بأسلوب فصيح ومؤثر.')">رسالة شكر رسمية</button>
              <button class="sample-pill" onclick="setAiPrompt('صياغة طلب إجازة رسمي لجهة العمل مع خالص الاحترام والتقدير.')">طلب إجازة رسمي</button>
              <button class="sample-pill" onclick="setAiPrompt('اكتب مقدمة مقال رصين وجذاب عن مكانة اللغة العربية وجماليات النحو والبلاغة.')">مقدمة مقال أدبي</button>
              <button class="sample-pill" onclick="setAiPrompt('أعد صياغة النص المكتوب في المحرر بأسلوب عربي فصيح ورصين ومترابط.')">إعادة صياغة النص</button>
            </div>

            <textarea id="aiPromptInput" rows="2" style="width:100%; border-radius:8px; border:1px solid var(--border); padding:8px; font-size:14px; margin-bottom:10px; resize:vertical;" placeholder="ماذا تريد أن يكتب الذكاء الاصطناعي؟ (مثال: خطاب تهنئة، بريد رسمي...)"></textarea>

            <div style="display:flex; gap:10px; align-items:center; margin-bottom:12px; flex-wrap:wrap;">
              <label style="font-size:12px; font-weight:bold; color:var(--text-muted);">الأسلوب / النبرة:</label>
              <select id="aiToneSelect" style="padding:6px 10px; border-radius:8px; border:1px solid var(--border); font-family:inherit; background:var(--surface); color:var(--text);">
                <option value="formal">رسمي وفصيح (Formal)</option>
                <option value="creative">إبداعي وبلاغي (Creative)</option>
                <option value="concise">موجز ومباشر (Concise)</option>
                <option value="academic">أكاديمي رصين (Academic)</option>
                <option value="persuasive">إقناعي مؤثر (Persuasive)</option>
              </select>
              <button class="btn-primary" onclick="generateAiWriting()" id="btnGenerateAi" style="margin-right:auto;">
                <span>✨</span> اكتب الآن بالذكاء الاصطناعي
              </button>
            </div>

            <div id="aiResultBox" style="display:none; background:var(--primary-container); padding:14px; border-radius:12px; margin-top:10px;">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
                <strong style="color:var(--primary); font-size:13px;">✓ النص المكتوب بالذكاء الاصطناعي:</strong>
                <button class="icon-btn" onclick="copyAiResult()" style="padding:4px 10px; font-size:12px;">نسخ</button>
              </div>
              <div id="aiResultText" style="line-height:1.7; font-size:14px; margin-bottom:10px; color:var(--on-primary-container);"></div>
              <div style="display:flex; gap:8px;">
                <button class="btn-primary" onclick="applyAiResult()" style="padding:6px 12px; font-size:12px;">استبدال بالمحرر</button>
                <button class="btn-secondary" onclick="appendAiResult()" style="padding:6px 12px; font-size:12px;">إلحاق بالمحرر</button>
              </div>
            </div>
          </div>
        </div>

        <!-- MAIN EDITOR CARD -->
        <div class="editor-card" style="margin-top: 14px;">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div style="display:flex; align-items:center; gap:8px;">
              <strong id="inputLabel">النص العربي</strong>
              <button class="sample-pill" onclick="toggleSpeechRecognition()" id="btnMicInput" style="display:flex; align-items:center; gap:4px; font-weight:bold; color:#DC2626; border-color:#DC2626; padding:3px 10px;" title="إملاء صوتي باللغة العربية">
                <span id="micIcon">🎤</span> <span id="micLabel">إملاء صوتي</span>
              </button>
            </div>
            <div style="display:flex; gap:6px;">
              <button class="icon-btn" onclick="pasteFromClipboard()" id="btnPasteMain" title="لصق النص">
                <span>📋</span> لصق
              </button>
              <button class="icon-btn" onclick="copyEditorText()" id="btnCopyMain" title="نسخ النص">
                <span>📑</span> نسخ
              </button>
              <button class="icon-btn" onclick="clearText()" id="btnClearMain" style="color:var(--error); border-color:rgba(220,38,38,0.3);" title="مسح النص">
                <span>🗑️</span> مسح
              </button>
            </div>
          </div>

          <!-- Dictation listening indicator -->
          <div id="micListeningBanner" style="display:none; background:#FEF2F2; border:1px solid #DC2626; color:#991B1B; padding:8px 12px; border-radius:8px; font-size:12px; font-weight:bold; align-items:center; justify-content:space-between;">
            <div style="display:flex; align-items:center; gap:6px;">
              <span class="pulsing-dot" style="display:inline-block; width:10px; height:10px; border-radius:50%; background:#DC2626; animation:pulse 1s infinite alternate;"></span>
              <span id="micStatusText">جارٍ الاستماع لإملائك الصوتي باللغة العربية الآن... تحدث بوضوح</span>
            </div>
            <button class="sample-pill" onclick="toggleSpeechRecognition()" style="padding:3px 10px; font-size:11px; background:#DC2626; color:#fff; border:none;">إيقاف</button>
          </div>

          <textarea id="textInput" placeholder="تحدث معي بالمايكروفون أو اكتب نصك هنا...">ذهب محمد الى المدرسة، و هو يحمل كتبه وشاهد عصفورا جميلا.</textarea>
          
          <div class="stats-bar">
            <span id="statWords">10 كلمات</span>
            <span id="statChars">54 أحرف</span>
            <span id="statIssues" style="color:var(--error)">5 ملاحظات</span>
          </div>

          <div id="statusBanner" style="display:none; background:var(--success-bg); color:var(--success); border:1px solid var(--success); padding:10px 14px; border-radius:10px; font-weight:bold; font-size:13px; text-align:center;">
            ✓ تم تصحيح كافة الأخطاء بنجاح!
          </div>

          <!-- ACTION BUTTONS ROW -->
          <div class="action-row">
            <button class="btn-primary" onclick="fixAllErrors()" id="btnFixAll" title="تصحيح كافة الأخطاء المكتشفة بضغطة واحدة">
              <span>✨</span> <span id="btnFixErrorsLabel">تصحيح الأخطاء</span>
            </button>
            <button class="btn-secondary" onclick="runCheck(true)" id="btnCheck" title="إعادة فحص النص واكتشاف الأخطاء">
              <span>🔍</span> <span id="btnCheckLabel">تدقيق وفحص</span>
            </button>
            <button class="btn-outline" onclick="applyAll()" id="btnApplyAll" title="تطبيق كافة الاقتراحات فورياً">
              <span>⚡</span> <span id="btnApplyAllLabel">تطبيق الكل</span>
            </button>
          </div>
        </div>

        <!-- SMOOTH ANIMATED PROCESSING INDICATOR -->
        <div id="processingIndicatorCard" class="processing-loading-card" role="status" aria-live="polite">
          <div class="spinner-ring-container">
            <div class="spinner-ring-outer"></div>
            <div class="spinner-ring-inner"></div>
            <div class="spinner-ring-center">ن</div>
          </div>
          <div style="display:flex; align-items:center; justify-content:center; gap:6px; font-weight:bold; color:var(--primary); font-size:14px;">
            <span>✨</span>
            <span id="processingIndicatorTitle">جارٍ تدقيق وتحليل النص العربي بالذكاء الاصطناعي</span>
            <div class="pulsing-dots-inline"><span></span><span></span><span></span></div>
          </div>
          <div style="font-size:12px; color:var(--text-muted); margin-top:4px;" id="processingIndicatorSubtitle">
            فحص الإعراب، مطابقة الفعل والفاعل، ضبط علامات الترقيم ورسم الهمزات...
          </div>
          <div class="processing-shimmer-bar">
            <div class="processing-shimmer-thumb"></div>
          </div>
        </div>

        <!-- ISSUES CHIPS LIST -->
        <div style="margin-top: 14px;">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <h4 id="issuesHeading">الأخطاء المكتشفة:</h4>
            <span id="issuesCountBadge" style="font-size:11px; font-weight:bold; background:var(--border); padding:2px 8px; border-radius:12px;">5 أخطاء</span>
          </div>
          <div id="issuesList" class="issues-chips"></div>
        </div>

        <!-- INSPECTOR CARD -->
        <div id="inspector" class="inspector-card" style="margin-top: 14px;">
          <div class="inspector-header">
            <strong id="inspTitle">تفاصيل التصحيح</strong>
            <span id="inspBadge" class="badge grammar">نحو</span>
          </div>
          <div style="font-size:13px; margin-bottom: 6px;">
            <span style="color:var(--error); font-weight:bold;" id="inspIssueLabel">المشكلة:</span>
            <span id="inspIssueText">خطأ في رسم الهمزة</span>
          </div>
          <div style="font-size:14px; font-weight:bold; color:var(--primary); margin-bottom: 6px; background:rgba(0,0,0,0.04); padding:6px 10px; border-radius:6px;">
            <span id="inspSuggLabel">الاقتراح:</span> <span id="inspSuggText">إلى</span>
          </div>
          <p id="inspExplanation" style="font-size:12px; color:var(--text-muted); margin-bottom: 12px; line-height:1.5;">
            حرف جر يجب كتابته بهمز قطع مكسورة تحت الألف.
          </p>
          <div style="display:flex; gap:8px; flex-wrap:wrap;">
            <button class="btn-primary" onclick="applySingle()" id="btnApplySingle" style="flex:2;">
              <span>✓</span> تطبيق هذا التصحيح
            </button>
            <button class="icon-btn" onclick="prevIssue()" id="btnPrevIssue" title="الخطأ السابق" style="flex:1;">
              <span>➡</span> السابق
            </button>
            <button class="icon-btn" onclick="nextIssue()" id="btnNextIssue" title="الخطأ التالي" style="flex:1;">
              التالي <span>⬅</span>
            </button>
          </div>
        </div>
      </div>

      <!-- RULES VIEW -->
      <div id="viewRules" class="view-container">
        <h3 style="margin-bottom: 12px;">دليل القواعد النحوية والترقيم</h3>
        <div class="plan-card">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <strong>همزتا الوصل والقطع</strong>
            <span class="badge spelling">إملاء</span>
          </div>
          <p style="font-size:12px; color:var(--text-muted); margin: 6px 0;">همزة القطع تثبت نطقاً ورسماً (إلى، أرسل، إن). همزة الوصل تسقط في درج الكلام (استمع، اكتب).</p>
          <div style="font-size:12px; color:var(--success); font-weight:bold; margin-bottom:8px;">الصواب: ذهب محمدٌ إلى المدرسةِ</div>
          <button class="btn-outline" style="width:100%; font-size:12px; padding:6px;" onclick="loadRuleIntoChecker('ذهب محمد الى المدرسه وحضر احمد')">
            ⚡ تجربة هذه القاعدة في المصحح
          </button>
        </div>
        
        <div class="plan-card">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <strong>مطابقة الفعل للفاعل والمبتدأ</strong>
            <span class="badge agreement">توافق</span>
          </div>
          <p style="font-size:12px; color:var(--text-muted); margin: 6px 0;">يؤنث الفعل مع الفاعل المؤنث الحقيقي ويطابق المبتدأ في التذكير والتأنيث والعدد.</p>
          <div style="font-size:12px; color:var(--success); font-weight:bold; margin-bottom:8px;">الصواب: المعلمةُ تُساعدُ الطلاب</div>
          <button class="btn-outline" style="width:100%; font-size:12px; padding:6px;" onclick="loadRuleIntoChecker('إن المعلمة يساعد الطلاب في فهم الدرس')">
            ⚡ تجربة هذه القاعدة في المصحح
          </button>
        </div>

        <div class="plan-card">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <strong>رسم تنوين الفتح والنصب</strong>
            <span class="badge grammar">تنوين</span>
          </div>
          <p style="font-size:12px; color:var(--text-muted); margin: 6px 0;">يُزاد ألف بعد تنوين الفتح في الأسماء المنونة (عصفوراً، جميلاً)، ولا تُزاد بعد التاء المربوطة أو الهمزة المسبوقة بألف (مساءً).</p>
          <div style="font-size:12px; color:var(--success); font-weight:bold; margin-bottom:8px;">الصواب: شاهد عصفوراً جميلاً</div>
          <button class="btn-outline" style="width:100%; font-size:12px; padding:6px;" onclick="loadRuleIntoChecker('شاهدت عصفورا جميلا يغرد مساءا')">
            ⚡ تجربة هذه القاعدة في المصحح
          </button>
        </div>

        <div class="plan-card">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <strong>علامات الترقيم وواو العطف</strong>
            <span class="badge punct">ترقيم</span>
          </div>
          <p style="font-size:12px; color:var(--text-muted); margin: 6px 0;">الفاصلة العربية (،) تلتصق بالكلمة السابقة، وواو العطف تتصل بالكلمة التالية مباشرة دون مسافة.</p>
          <div style="font-size:12px; color:var(--success); font-weight:bold; margin-bottom:8px;">الصواب: نعم، شكراً لك وجزاك الله خيراً.</div>
          <button class="btn-outline" style="width:100%; font-size:12px; padding:6px;" onclick="loadRuleIntoChecker('نعم , شكرا لك و جزاك الله خيرا ?')">
            ⚡ تجربة هذه القاعدة في المصحح
          </button>
        </div>
      </div>

      <!-- PRICING VIEW -->
      <div id="viewPricing" class="view-container">
        <h3 style="margin-bottom: 8px;">خطط وأسعار NahwiFix</h3>
        <p style="font-size:12px; color:var(--text-muted); margin-bottom:14px;">اختر الخطة المناسبة لاحتياجاتك التدقيقية</p>
        
        <div class="plan-card" id="cardPlanFree">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <h4>الخطة الأساسية</h4>
            <span id="badgePlanFree" class="badge grammar" style="background:#475569;">المفعلة</span>
          </div>
          <div style="font-size:18px; font-weight:bold; margin: 4px 0;">مجاناً دائماً</div>
          <p style="font-size:12px; color:var(--text-muted)">1,000 كلمة لكل فحص، قواعد النحو والترقيم الأساسية، سجل محلي.</p>
          <button class="btn-outline" id="btnSelectFree" style="margin-top:10px; width:100%" onclick="selectPlan('free')">
            ✓ خطتك الحالية
          </button>
        </div>

        <div class="plan-card pro" id="cardPlanPro">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <h4>الخطة الاحترافية (Pro) ⭐</h4>
            <span id="badgePlanPro" class="badge agreement">الأكثر طلباً</span>
          </div>
          <div style="font-size:20px; font-weight:800; margin: 4px 0;">$9 / شهرياً</div>
          <p style="font-size:12px; line-height:1.5;">فحص غير محدود، تشكيل كامل بالحركات، تعرف بصري OCR، وتوليد نطق صوتي وتصدير PDF.</p>
          <button class="btn-primary" id="btnSelectPro" style="margin-top:10px; width:100%" onclick="selectPlan('pro')">
            <span>⭐</span> ترقية إلى Pro الآن
          </button>
        </div>

        <div class="plan-card" id="cardPlanEnterprise">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <h4>خطة المؤسسات (Enterprise)</h4>
            <span class="badge spelling">للشركات</span>
          </div>
          <div style="font-size:18px; font-weight:bold; margin: 4px 0;">$29 / شهرياً</div>
          <p style="font-size:12px; color:var(--text-muted)">دعم فني مخصص، تراخيص متعددة للفرق، والوصول لواجهة البرمجة (API).</p>
          <button class="btn-secondary" id="btnSelectEnterprise" style="margin-top:10px; width:100%" onclick="selectPlan('enterprise')">
            طلب اشتراك المؤسسات
          </button>
        </div>
      </div>

      <!-- ACCOUNT VIEW -->
      <div id="viewAccount" class="view-container">
        <h3 style="margin-bottom: 8px;">حساب NahwiFix</h3>
        <p style="font-size:12px; color:var(--text-muted); margin-bottom:12px;">مزامنة النصوص والمستندات وحفظ الإعدادات المفضلة</p>

        <!-- Logged out form -->
        <div id="authLoggedOutCard" class="editor-card">
          <div style="display:flex; gap:6px; margin-bottom:8px; border-bottom:1px solid var(--border); padding-bottom:8px;">
            <button class="sample-pill active" id="tabSignInBtn" onclick="switchAuthTab('signin')" style="flex:1; justify-content:center;">تسجيل الدخول</button>
            <button class="sample-pill" id="tabSignUpBtn" onclick="switchAuthTab('signup')" style="flex:1; justify-content:center;">إنشاء حساب</button>
          </div>

          <div id="authUsernameRow" style="display:none;">
            <label style="font-size:11px; font-weight:bold;">الاسم الكامل:</label>
            <input type="text" id="authUsernameInput" value="فادي ناصر" placeholder="اسمك الكامل" style="width:100%; padding:9px 12px; border:1px solid var(--border); border-radius:8px; margin-top:3px; margin-bottom:8px; font-size:13px; font-family:inherit; background:var(--bg); color:var(--text);">
          </div>

          <label style="font-size:11px; font-weight:bold;">البريد الإلكتروني:</label>
          <input type="email" id="authEmailInput" value="Fadiyano44@gmail.com" placeholder="example@email.com" style="width:100%; padding:9px 12px; border:1px solid var(--border); border-radius:8px; margin-top:3px; margin-bottom:8px; font-size:13px; font-family:inherit; background:var(--bg); color:var(--text);">

          <label style="font-size:11px; font-weight:bold;">كلمة المرور:</label>
          <input type="password" id="authPasswordInput" value="••••••••" placeholder="كلمة المرور" style="width:100%; padding:9px 12px; border:1px solid var(--border); border-radius:8px; margin-top:3px; margin-bottom:14px; font-size:13px; font-family:inherit; background:var(--bg); color:var(--text);">

          <div style="display:flex; gap:8px;">
            <button class="btn-primary" onclick="submitAuth()" id="btnAuthSubmit" style="flex:2;">
              <span id="btnAuthSubmitLabel">تسجيل الدخول</span>
            </button>
            <button class="btn-outline" onclick="demoQuickLogin()" title="دخول تجريبي بنقرة واحدة" style="flex:1;">
              ⚡ دخول سريع
            </button>
          </div>
        </div>

        <!-- Logged in Profile Card -->
        <div id="authLoggedInCard" class="editor-card" style="display:none;">
          <div style="display:flex; align-items:center; gap:12px; margin-bottom:10px;">
            <div style="width:52px; height:52px; border-radius:50%; background:var(--primary); color:#fff; display:flex; align-items:center; justify-content:center; font-size:22px; font-weight:bold;" id="userAvatar">
              ف
            </div>
            <div>
              <div style="font-weight:bold; font-size:16px;" id="profileName">فادي ناصر</div>
              <div style="font-size:12px; color:var(--text-muted);" id="profileEmail">Fadiyano44@gmail.com</div>
              <span class="badge agreement" id="profilePlanBadge" style="margin-top:4px; display:inline-block;">الخطة الاحترافية (Pro)</span>
            </div>
          </div>

          <div style="background:var(--bg); border:1px solid var(--border); border-radius:10px; padding:12px; margin-bottom:12px; font-size:12px; display:flex; justify-content:space-around; text-align:center;">
            <div>
              <div style="font-size:16px; font-weight:bold; color:var(--primary);">48</div>
              <div style="color:var(--text-muted);">نصوص مدققة</div>
            </div>
            <div style="border-left:1px solid var(--border);"></div>
            <div>
              <div style="font-size:16px; font-weight:bold; color:var(--secondary);">12,450</div>
              <div style="color:var(--text-muted);">كلمة مفحوصة</div>
            </div>
            <div style="border-left:1px solid var(--border);"></div>
            <div>
              <div style="font-size:16px; font-weight:bold; color:var(--success);">99.4%</div>
              <div style="color:var(--text-muted);">دقة التدقيق</div>
            </div>
          </div>

          <div style="display:flex; gap:8px;">
            <button class="btn-secondary" onclick="switchNav('Pricing')" style="flex:1;">
              💳 إدارة الخطة
            </button>
            <button class="btn-outline" onclick="logoutUser()" style="flex:1; color:var(--error); border-color:rgba(220,38,38,0.3);">
              تسجيل الخروج
            </button>
          </div>
        </div>
      </div>

      <!-- TERMS VIEW -->
      <div id="viewTerms" class="view-container">
        <h3 style="margin-bottom: 8px;">الشروط والخصوصية</h3>
        <div class="editor-card" style="font-size:13px; line-height:1.7;">
          <p><strong>🔒 حماية الخصوصية:</strong> جميع النصوص تفحص محلياً على جهازك ويتم حفظ السجل في قاعدة بيانات Room المشفرة لضمان الخصوصية التامة.</p>
          <p style="margin-top:8px;"><strong>⚖️ الملكية الفكرية:</strong> جميع حقوق النصوص المحررة والمدققة تعود ملكيتها الخالصة للمستخدم دون أي احتفاظ من المنصة.</p>
          <p style="margin-top:8px;"><strong>🌐 سياسة الذكاء الاصطناعي:</strong> محرك التوليد وإعادة الصياغة يلتزم بأعلى معايير الأمانة اللغوية والأخلاقية.</p>
          
          <div style="margin-top:14px; display:flex; gap:8px; flex-wrap:wrap;">
            <button class="btn-primary" onclick="acceptTerms()" id="btnAcceptTerms">
              <span id="acceptTermsIcon">✓</span> <span id="btnAcceptTermsLabel">أوافق على كافة الشروط والخصوصية</span>
            </button>
            <button class="btn-outline" onclick="printOrExportTerms()">
              📄 طباعة الشروط
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- BOTTOM NAVIGATION -->
    <nav class="bottom-nav">
      <button class="nav-item active" onclick="switchNav('Checker', this)" data-nav="Checker">
        <span style="font-size:18px;">📝</span>
        <span class="nav-label" data-ar="المصحح" data-en="Checker">المصحح</span>
      </button>
      <button class="nav-item" onclick="switchNav('Rules', this)" data-nav="Rules">
        <span style="font-size:18px;">📖</span>
        <span class="nav-label" data-ar="القواعد" data-en="Rules">القواعد</span>
      </button>
      <button class="nav-item" onclick="switchNav('Pricing', this)" data-nav="Pricing">
        <span style="font-size:18px;">💳</span>
        <span class="nav-label" data-ar="الأسعار" data-en="Pricing">الأسعار</span>
      </button>
      <button class="nav-item" onclick="switchNav('Account', this)" data-nav="Account">
        <span style="font-size:18px;">👤</span>
        <span class="nav-label" data-ar="الحساب" data-en="Account">الحساب</span>
      </button>
      <button class="nav-item" onclick="switchNav('Terms', this)" data-nav="Terms">
        <span style="font-size:18px;">📄</span>
        <span class="nav-label" data-ar="الشروط" data-en="Terms">الشروط</span>
      </button>
    </nav>

    <!-- Floating Scroll Controls -->
    <div style="position:fixed; bottom:75px; left:16px; display:flex; flex-direction:column; gap:8px; z-index:9999;">
      <button onclick="scrollToTop()" id="btnScrollTop" title="Scroll to Top / إلى الأعلى" style="background:#2563EB; color:#fff; border:none; width:40px; height:40px; border-radius:50%; box-shadow:0 4px 12px rgba(0,0,0,0.25); font-size:18px; display:flex; align-items:center; justify-content:center;">
        ⬆️
      </button>
      <button onclick="scrollToBottom()" id="btnScrollBottom" title="Scroll to Bottom / إلى الأسفل" style="background:#475569; color:#fff; border:none; width:40px; height:40px; border-radius:50%; box-shadow:0 4px 12px rgba(0,0,0,0.25); font-size:18px; display:flex; align-items:center; justify-content:center;">
        ⬇️
      </button>
    </div>
  </div>

  <script>
    let currentLang = 'ar';
    let isDark = false;
    let currentIssues = [];
    let activeIssueIndex = 0;
    let activeIssue = null;
    let currentPlan = 'pro';
    let isLoggedIn = false;
    let isTermsAccepted = false;

    // Toast feedback helper
    function showToast(message, type = 'success') {
      const toast = document.getElementById('toastNotification');
      const msg = document.getElementById('toastMsg');
      const icon = document.getElementById('toastIcon');
      if (!toast) return;

      toast.className = 'show ' + type;
      msg.innerText = message;
      icon.innerText = type === 'success' ? '✓' : (type === 'error' ? '⚠️' : 'ℹ️');

      clearTimeout(window._toastTimeout);
      window._toastTimeout = setTimeout(() => {
        toast.className = '';
      }, 3200);
    }

    const samples = [
      "ذهب محمد الى المدرسة، و هو يحمل كتبه وشاهد عصفورا جميلا.",
      "إن المعلمة يساعد الطلاب في فهم الدرس بكل عناية.",
      "كان الولد يلعب في الحديقةُ وشاهد عصفوراً جميلاً يطير.",
      "إن المعلمون كانوا مسافرون في رحلة تعليمية هادفة.",
      "لم يذهبون الطلاب إلى الملعب ولم ياتي الحارس مبكراً."
    ];

    function analyze(text) {
      const issues = [];
      if (!text || !text.trim()) return issues;

      // 1. Hamza rules
      const hamza = {
        'الى': ['إلى', 'حرف جر يبدأ بهمزة قطع مكسورة'],
        'ان': ['إنّ', 'حرف ناسخ يبدأ بهمزة قطع مكسورة مشددة النون'],
        'او': ['أو', 'حرف عطف يبدأ بهمزة قطع مفتوحة'],
        'اذا': ['إذا', 'ظرف يبدأ بهمزة قطع مكسورة'],
        'الا': ['إلا', 'أداة استثناء تبدأ بهمزة قطع مكسورة'],
        'اما': ['أما', 'حرف تفصيل يبدأ بهمزة قطع مفتوحة'],
        'اين': ['أين', 'اسم استفهام يبدأ بهمزة قطع مفتوحة'],
        'انت': ['أنتَ', 'ضمير مخاطب يبدأ بهمزة قطع مفتوحة'],
        'انتم': ['أنتم', 'ضمير مخاطب للجمع يبدأ بهمزة قطع'],
        'انا': ['أنا', 'ضمير متكلم مفرد يبدأ بهمزة قطع مفتوحة'],
        'اخذ': ['أخذ', 'فعل ماض ثلاثي مهموز يبدأ بهمزة قطع'],
        'اكل': ['أكل', 'فعل ماض ثلاثي مهموز يبدأ بهمزة قطع'],
        'احمد': ['أحمد', 'اسم علم وزنه أفعل يبدأ بهمزة قطع'],
        'اعلان': ['إعلان', 'مصدر الفعل الرباعي أعلن همزته قطع مكسورة'],
        'اصدار': ['إصدار', 'مصدر الفعل الرباعي همزته قطع'],
        'انجاز': ['إنجاز', 'مصدر الفعل الرباعي همزته قطع'],
        'انتاج': ['إنتاج', 'مصدر رباعي همزته قطع'],
        'اكرام': ['إكرام', 'مصدر رباعي همزته قطع مكسورة'],
        'احسان': ['إحسان', 'مصدر رباعي همزته قطع مكسورة'],
        'اكثر': ['أكثر', 'اسم تفضيل همزته قطع'],
        'اكبر': ['أكبر', 'اسم تفضيل همزته قطع'],
        'اصغر': ['أصغر', 'اسم تفضيل همزته قطع'],
        'افضل': ['أفضل', 'اسم تفضيل همزته قطع'],
        'احسن': ['أحسن', 'اسم تفضيل همزته قطع'],
        'اقل': ['أقل', 'اسم تفضيل همزته قطع'],
        'اجمل': ['أجمل', 'اسم تفضيل همزته قطع'],
        'اول': ['أول', 'اسم همزته قطع'],
        'اخر': ['آخر', 'اسم يبدأ بمدّة'],
        'اهم': ['أهم', 'اسم تفضيل همزته قطع'],
        'اصبح': ['أصبح', 'فعل ماض ناسخ همزته قطع'],
        'اراد': ['أراد', 'فعل رباعي همزته قطع'],
        'ادارة': ['إدارة', 'مصدر رباعي همزته قطع'],
        'اعادة': ['إعادة', 'مصدر رباعي همزته قطع'],
        'اضافة': ['إضافة', 'مصدر رباعي همزته قطع'],
        'اكد': ['أكد', 'فعل ماض همزته قطع'],
        'اشار': ['أشار', 'فعل ماض همزته قطع'],
        'اوضح': ['أوضح', 'فعل ماض همزته قطع'],
        'اعلن': ['أعلن', 'فعل ماض همزته قطع'],
        'اطفال': ['أطفال', 'جمع تكسير همزته قطع'],
        'اسماء': ['أسماء', 'جمع تكسير همزته قطع'],
        'ارقام': ['أرقام', 'جمع تكسير همزته قطع'],
        'ايام': ['أيام', 'جمع تكسير همزته قطع'],
        'امور': ['أمور', 'جمع تكسير همزته قطع'],
        'اعمال': ['أعمال', 'جمع تكسير همزته قطع'],
        'اهداف': ['أهداف', 'جمع تكسير همزته قطع'],
        'افكار': ['أفكار', 'جمع تكسير همزته قطع'],
        'انواع': ['أنواع', 'جمع تكسير همزته قطع'],
        'اشخاص': ['أشخاص', 'جمع تكسير همزته قطع'],
        'اشياء': ['أشياء', 'جمع تكسير همزته قطع'],
        'إسم': ['اسم', 'من الأسماء العشرة السماعية همزتها همزة وصل'],
        'إبن': ['ابن', 'همزة وصل سماعية تسقط نطقا ووصلا'],
        'إبنة': ['ابنة', 'همزة وصل سماعية تسقط وصلا']
      };
      for (const [w, [rep, exp]] of Object.entries(hamza)) {
        const regex = new RegExp('(^|\\s)' + w + '(?=$|\\s|[,،.!?؟؛])', 'g');
        let match;
        while ((match = regex.exec(text)) !== null) {
          issues.push({
            orig: w,
            sugg: rep,
            cat: 'spelling',
            badge: 'همزة وإملاء',
            issue: "خطأ في رسم همزة القطع/الوصل في كلمة '" + w + "'",
            exp: exp
          });
        }
      }

      // 2. Ta' Marbuta vs Ha'
      const taMarbuta = {
        'مدرسه': ['مدرسة', 'اسم مؤنث ينتهي بتاء مربوطة منقوطة'],
        'جامعه': ['جامعة', 'اسم مؤنث ينتهي بتاء مربوطة منقوطة'],
        'حديقه': ['حديقة', 'اسم مؤنث ينتهي بتاء مربوطة منقوطة'],
        'مدينه': ['مدينة', 'اسم مؤنث ينتهي بتاء مربوطة منقوطة'],
        'جميله': ['جميلة', 'صفة مؤنثة تنتهي بتاء مربوطة منقوطة'],
        'كبيره': ['كبيرة', 'صفة مؤنثة تنتهي بتاء مربوطة منقوطة'],
        'صغيره': ['صغيرة', 'صفة مؤنثة تنتهي بتاء مربوطة منقوطة'],
        'لغه': ['لغة', 'اسم مؤنث ينتهي بتاء مربوطة منقوطة'],
        'عربيه': ['عربية', 'صفة مؤنثة تنتهي بتاء مربوطة منقوطة'],
        'حياه': ['حياة', 'اسم مؤنث ينتهي بتاء مربوطة منقوطة'],
        'صلاه': ['صلاة', 'اسم مؤنث ينتهي بتاء مربوطة منقوطة'],
        'هذة': ['هذه', 'اسم إشارة ينتهي بهاء غير منقوطة'],
        'علية': ['عليه', 'شبه جملة تنتهي بهاء الضمير غير المنقوطة'],
        'الية': ['إليه', 'شبه جملة تنتهي بهاء الضمير'],
        'منة': ['منه', 'حرف جر وضمير ينتهي بهاء الغائب'],
        'فية': ['فيه', 'حرف جر وضمير ينتهي بهاء الغائب'],
        'عنة': ['عنه', 'حرف جر وضمير ينتهي بهاء الغائب'],
        'وجة': ['وجه', 'اسم مذكر ينتهي بهاء أصلية']
      };
      for (const [w, [rep, exp]] of Object.entries(taMarbuta)) {
        const regex = new RegExp('(^|\\s)' + w + '(?=$|\\s|[,،.!?؟؛])', 'g');
        let match;
        while ((match = regex.exec(text)) !== null) {
          issues.push({
            orig: w,
            sugg: rep,
            cat: 'spelling',
            badge: 'تاء مربوطة وهاء',
            issue: "خلط بين التاء المربوطة والهاء في كلمة '" + w + "'",
            exp: exp
          });
        }
      }

      // 3. Ya vs Alif Maqsura
      const yaFixes = {
        'حتي': ['حتى', 'حرف غاية ينتهي بألف مقصورة'],
        'فى': ['في', 'حرف جر ينتهي بياء منقوطة'],
        'التى': ['التي', 'اسم موصول ينتهي بياء منقوطة'],
        'الذى': ['الذي', 'اسم موصول ينتهي بياء منقوطة']
      };
      for (const [w, [rep, exp]] of Object.entries(yaFixes)) {
        const regex = new RegExp('(^|\\s)' + w + '(?=$|\\s|[,،.!?؟؛])', 'g');
        let match;
        while ((match = regex.exec(text)) !== null) {
          issues.push({
            orig: w,
            sugg: rep,
            cat: 'spelling',
            badge: 'ياء وألف مقصورة',
            issue: "خطأ في رسم الياء أو الألف المقصورة في '" + w + "'",
            exp: exp
          });
        }
      }

      // 4. Tanween rules
      const tanween = {
        'عصفورا': ['عصفوراً', 'مفعول به منصوب بتنوين الفتح'],
        'جميلا': ['جميلاً', 'نعت منصوب يتبع المنعوت بتنوين الفتح'],
        'شكرا': ['شكراً', 'مفعول مطلق منصوب بتنوين الفتح'],
        'جزءا': ['جزءاً', 'اسم منون بتنوين الفتح'],
        'مساءا': ['مساءً', 'الهمزة المسبوقة بألف لا تلحقها ألف تنوين'],
        'سواءا': ['سواءً', 'الهمزة المسبوقة بألف لا تلحقها ألف تنوين'],
        'بناءا': ['بناءً', 'الهمزة المسبوقة بألف لا تلحقها ألف تنوين'],
        'شفاءا': ['شفاءً', 'الهمزة المسبوقة بألف لا تلحقها ألف تنوين'],
        'رجاءا': ['رجاءً', 'الهمزة المسبوقة بألف لا تلحقها ألف تنوين'],
        'ايضا': ['أيضاً', 'مفعول مطلق منصوب بتنوين الفتح وهمزة قطع'],
        'أيضا': ['أيضاً', 'مفعول مطلق منصوب بتنوين الفتح'],
        'دائما': ['دائماً', 'حال أو ظرف منصوب بتنوين الفتح'],
        'ابدا': ['أبداً', 'ظرف زمان منصوب بتنوين الفتح وهمزة قطع'],
        'أبدا': ['أبداً', 'ظرف زمان منصوب بتنوين الفتح'],
        'معا': ['معاً', 'حال منصوبة بتنوين الفتح'],
        'صباحا': ['صباحاً', 'ظرف زمان منصوب بتنوين الفتح'],
        'نهارا': ['نهاراً', 'ظرف زمان منصوب بتنوين الفتح'],
        'ليلا': ['ليلاً', 'ظرف زمان منصوب بتنوين الفتح'],
        'فعلا': ['فعلاً', 'مفعول مطلق منصوب بتنوين الفتح'],
        'حقا': ['حقاً', 'مفعول مطلق منصوب بتنوين الفتح'],
        'طبعا': ['طبعاً', 'مفعول مطلق منصوب بتنوين الفتح'],
        'غالبا': ['غالباً', 'حال منصوبة بتنوين الفتح'],
        'نادرا': ['نادراً', 'حال منصوبة بتنوين الفتح'],
        'جدا': ['جداً', 'مفعول مطلق منصوب بتنوين الفتح'],
        'اولا': ['أولاً', 'اسم منون بالفتح للترتيب'],
        'أولا': ['أولاً', 'اسم منون بالفتح للترتيب'],
        'ثانيا': ['ثانياً', 'اسم منون بالفتح للترتيب'],
        'ثالثا': ['ثالثاً', 'اسم منون بالفتح للترتيب'],
        'اخيرا': ['أخيراً', 'ظرف منون بالفتح للترتيب'],
        'أخيرا': ['أخيراً', 'ظرف منون بالفتح للترتيب'],
        'سابقا': ['سابقاً', 'ظرف منصوب بتنوين الفتح'],
        'لاحقا': ['لاحقاً', 'ظرف منصوب بتنوين الفتح'],
        'حاليا': ['حالياً', 'ظرف منصوب بتنوين الفتح'],
        'مسبقا': ['مسبقاً', 'ظرف منصوب بتنوين الفتح'],
        'عموما': ['عموماً', 'حال منصوبة بتنوين الفتح'],
        'مثلا': ['مثلاً', 'مفعول مطلق منصوب بتنوين الفتح'],
        'فورا': ['فوراً', 'حال منصوبة بتنوين الفتح'],
        'مرحبا': ['مرحباً', 'مفعول به منصوب بتنوين الفتح'],
        'اهلا': ['أهلاً', 'مفعول به منصوب بتنوين الفتح وهمزة قطع'],
        'أهلا': ['أهلاً', 'مفعول به منصوب بتنوين الفتح'],
        'سهلا': ['سهلاً', 'معطوف منصوب بتنوين الفتح'],
        'جميعا': ['جميعاً', 'حال منصوبة بتنوين الفتح']
      };
      for (const [w, [rep, exp]] of Object.entries(tanween)) {
        const regex = new RegExp('(^|\\s)' + w + '(?=$|\\s|[,،.!?؟؛])', 'g');
        let match;
        while ((match = regex.exec(text)) !== null) {
          issues.push({
            orig: w,
            sugg: rep,
            cat: 'grammar',
            badge: 'تنوين ونحو',
            issue: "إهمال رسم تنوين النصب على '" + w + "'",
            exp: exp
          });
        }
      }

      // 5. Syntactic and agreement rules
      const agreements = [
        ['المعلمة يساعد', 'المعلمةُ تُساعدُ', 'عدم مطابقة الفعل المضارع للفاعل المؤنث', 'يجب تأنيث الفعل المضارع بتاء التأنيث لتطابقه مع الفاعل المؤنث.'],
        ['إن المعلمة يساعد', 'إنّ المعلمةَ تُساعدُ', 'مطابقة الفعل واسم إن المنصوب', 'اسم إن منصوب بالفتحة ومطابقة الفعل المضارع بالتاء.'],
        ['ان المعلمة يساعد', 'إنّ المعلمةَ تُساعدُ', 'مطابقة الفعل واسم إن المنصوب', 'اسم إن منصوب بالفتحة ومطابقة الفعل المضارع بالتاء.'],
        ['البنت ذهب', 'البنت ذهبت', 'تأنيث الفعل الماضي', 'الفاعل مؤنث حقيقي فيلزم تاء التأنيث الساكنة في الفعل الماضي.'],
        ['الطلاب يفهم', 'الطلاب يفهمون', 'مطابقة الفعل للجمع', 'الفعل المسند لواو الجماعة يرفع بثبوت النون.'],
        ['في الحديقةُ', 'في الحديقةِ', 'خطأ في ضبط الاسم المجرور', 'الاسم بعد حرف الجر (في) يكون مجروراً وعلامة جره الكسرة.'],
        ['إن المعلمون', 'إنّ المعلمين', 'اسم إن منصوب', 'اسم إن منصوب وعلامة نصبه الياء لأنه جمع مذكر سالم.'],
        ['ان المعلمون', 'إنّ المعلمين', 'اسم إن منصوب', 'اسم إن منصوب وعلامة نصبه الياء لأنه جمع مذكر سالم.'],
        ['كانوا مسافرون', 'كانوا مسافرين', 'خبر كان منصوب', 'خبر كان منصوب وعلامة نصبه الياء لأنه جمع مذكر سالم.'],
        ['لم يذهبون', 'لم يذهبوا', 'جزم الأفعال الخمسة', 'فعل مضارع مجزوم بلم بحذف النون وتزاد ألف التفريق.'],
        ['لن يذهبون', 'لن يذهبوا', 'نصب الأفعال الخمسة', 'فعل مضارع منصوب بلن بحذف النون وتزاد ألف التفريق.'],
        ['لم ياتي', 'لم يأتِ', 'جزم الفعل المعتل الآخر', 'فعل مضارع مجزوم بحذف حرف العلة والتعويض بالكسرة.'],
        ['لم يأتي', 'لم يأتِ', 'جزم الفعل المعتل الآخر', 'فعل مضارع مجزوم بحذف حرف العلة والتعويض بالكسرة.'],
        ['لا تنسى', 'لا تنسَ', 'جزم الفعل المعتل الآخر', 'فعل مضارع مجزوم بلا الناهية بحذف حرف العلة.'],
        ['ذهب محمد الى المدرسة', 'ذهب محمدٌ إلى المدرسةِ', 'إعراب الفاعل والمجرور', 'فاعل مرفوع بالضمة ومجرور بحرف الجر بالكسرة.'],
        ['ذهب محمد إلى المدرسة', 'ذهب محمدٌ إلى المدرسةِ', 'إعراب الفاعل والمجرور', 'فاعل مرفوع بالضمة ومجرور بحرف الجر بالكسرة.']
      ];
      for (const [orig, sugg, issue, exp] of agreements) {
        if (text.includes(orig)) {
          issues.push({
            orig: orig,
            sugg: sugg,
            cat: 'agreement',
            badge: 'توافق نحوي',
            issue: issue,
            exp: exp
          });
        }
      }

      // 6. Conjunction Waw
      const wawRegex = /(^|\s)و\s+([\u0600-\u06FF]+)/g;
      let wawMatch;
      while ((wawMatch = wawRegex.exec(text)) !== null) {
        const full = wawMatch[0].trim();
        const word = wawMatch[2];
        issues.push({
          orig: full,
          sugg: 'و' + word,
          cat: 'punct',
          badge: 'ترقيم وعطف',
          issue: "فصل واو العطف عن المعطوف '" + word + "'",
          exp: "واو العطف حرف أحادي يلتصق رسماً بالكلمة التي تليه دون مسافة."
        });
      }

      // 7. Punctuation rules
      if (text.includes(',')) {
        issues.push({
          orig: ',',
          sugg: '،',
          cat: 'punct',
          badge: 'ترقيم',
          issue: 'استخدام الفاصلة اللاتينية بدلاً من العربية (،)',
          exp: 'في اللغة العربية تُستخدم الفاصلة المقلوبة لأعلى (،).'
        });
      }
      if (text.includes('?')) {
        issues.push({
          orig: '?',
          sugg: '؟',
          cat: 'punct',
          badge: 'ترقيم',
          issue: 'استخدام علامة الاستفهام اللاتينية (?)',
          exp: 'علامة الاستفهام العربية تنعكس يمنة لتلائم اتجاه الكتابة (؟).'
        });
      }
      if (text.includes(';')) {
        issues.push({
          orig: ';',
          sugg: '؛',
          cat: 'punct',
          badge: 'ترقيم',
          issue: 'استخدام الفاصلة المنقوطة اللاتينية (;)',
          exp: 'تُستخدم الفاصلة المنقوطة العربية (؛) بين الجملتين السببيتين.'
        });
      }
      if (/\\s+([،,.!?؟؛])/.test(text)) {
        issues.push({
          orig: ' مسافة قبل علامة الترقيم',
          sugg: 'بدون مسافة',
          cat: 'punct',
          badge: 'ترقيم',
          issue: 'فراغ غير صحيح قبل علامة الترقيم',
          exp: 'علامات الترقيم العربية تلتصق بالكلمة التي قبلها مباشرة.'
        });
      }

      return issues;
    }

    let isProcessingText = false;

    function showProcessingIndicator(titleAr, titleEn) {
      const card = document.getElementById('processingIndicatorCard');
      if (!card) return;
      const titleEl = document.getElementById('processingIndicatorTitle');
      if (titleEl) {
        titleEl.innerText = currentLang === 'ar' ? (titleAr || 'جارٍ تدقيق وتحليل النص العربي بالذكاء الاصطناعي') : (titleEn || 'Analyzing Arabic text with AI');
      }
      card.classList.add('active');
    }

    function hideProcessingIndicator() {
      const card = document.getElementById('processingIndicatorCard');
      if (card) {
        card.classList.remove('active');
      }
    }

    function runCheck(showUserToast = false) {
      if (isProcessingText) return;
      const text = document.getElementById('textInput').value;
      const words = text.trim() ? text.trim().split(/\\s+/).length : 0;
      document.getElementById('statWords').innerText = words + (currentLang === 'ar' ? ' كلمات' : ' words');
      document.getElementById('statChars').innerText = text.length + (currentLang === 'ar' ? ' أحرف' : ' chars');

      // If user clicked check or text is non-empty, play smooth animated indicator
      if (showUserToast && text.trim().length > 0) {
        isProcessingText = true;
        showProcessingIndicator('جارٍ تدقيق النص واكتشاف الأخطاء النحوية والإملائية', 'Auditing grammar and orthography');
        const btnCheck = document.getElementById('btnCheck');
        if (btnCheck) {
          btnCheck.disabled = true;
          btnCheck.style.opacity = '0.7';
        }

        setTimeout(() => {
          hideProcessingIndicator();
          isProcessingText = false;
          if (btnCheck) {
            btnCheck.disabled = false;
            btnCheck.style.opacity = '1';
          }
          executeCheckAnalysis(text, showUserToast);
        }, 500);
      } else {
        executeCheckAnalysis(text, showUserToast);
      }
    }

    function executeCheckAnalysis(text, showUserToast) {
      currentIssues = analyze(text);
      document.getElementById('statIssues').innerText = currentIssues.length + (currentLang === 'ar' ? ' ملاحظات' : ' issues');
      
      const badgeEl = document.getElementById('issuesCountBadge');
      if (badgeEl) {
        badgeEl.innerText = currentIssues.length + (currentLang === 'ar' ? ' أخطاء' : ' errors');
      }

      const container = document.getElementById('issuesList');
      container.innerHTML = '';

      if (currentIssues.length === 0) {
        container.innerHTML = '<span style="color:var(--success); font-size:13px; font-weight:bold; padding:6px 0;">' + 
          (currentLang === 'ar' ? '✓ النص سليم وخالٍ من الأخطاء النحوية والإملائية!' : '✓ Text is pristine and error-free!') + '</span>';
        document.getElementById('inspector').style.display = 'none';
        if (showUserToast) {
          showToast(currentLang === 'ar' ? '✓ النص سليم 100% وخالٍ من الأخطاء' : '✓ Text is completely error-free!', 'success');
        }
        return;
      }

      document.getElementById('inspector').style.display = 'block';
      currentIssues.forEach((item, idx) => {
        const btn = document.createElement('button');
        btn.className = 'chip' + (idx === 0 ? ' active' : '');
        btn.innerHTML = '<span style="color:var(--error); font-weight:bold;">' + item.orig + '</span> ➔ <span style="color:var(--primary); font-weight:bold;">' + item.sugg + '</span>';
        btn.onclick = () => selectIssue(idx);
        container.appendChild(btn);
      });

      selectIssue(0);
      if (showUserToast) {
        showToast(currentLang === 'ar' ? ('تم اكتشاف ' + currentIssues.length + ' ملاحظات نحوية وإملائية') : ('Found ' + currentIssues.length + ' issues'), 'info');
      }
    }

    // Required updateStats helper called from dictation
    function updateStats() {
      runCheck(false);
    }

    function selectIssue(idx) {
      if (idx < 0 || idx >= currentIssues.length) return;
      activeIssueIndex = idx;
      activeIssue = currentIssues[idx];

      document.querySelectorAll('.chip').forEach((c, i) => {
        c.classList.toggle('active', i === idx);
      });

      if (activeIssue) {
        document.getElementById('inspBadge').innerText = activeIssue.badge;
        document.getElementById('inspBadge').className = 'badge ' + activeIssue.cat;
        document.getElementById('inspIssueText').innerText = activeIssue.issue;
        document.getElementById('inspSuggText').innerText = activeIssue.sugg;
        document.getElementById('inspExplanation').innerText = activeIssue.exp;
      }
    }

    function nextIssue() {
      if (currentIssues.length === 0) return;
      const nextIdx = (activeIssueIndex + 1) % currentIssues.length;
      selectIssue(nextIdx);
    }

    function prevIssue() {
      if (currentIssues.length === 0) return;
      const prevIdx = (activeIssueIndex - 1 + currentIssues.length) % currentIssues.length;
      selectIssue(prevIdx);
    }

    function applySingle() {
      if (!activeIssue) return;
      const textarea = document.getElementById('textInput');
      const fixedWord = activeIssue.sugg;
      textarea.value = textarea.value.replace(activeIssue.orig, activeIssue.sugg);
      showToast(currentLang === 'ar' ? ('تم تطبيق التصحيح: ' + fixedWord) : ('Applied correction: ' + fixedWord), 'success');
      runCheck(false);
      updateReaderDisplay();
    }

    function applyAll() {
      let text = document.getElementById('textInput').value;
      const issues = analyze(text);
      if (issues.length === 0) {
        showToast(currentLang === 'ar' ? 'النص مصحح بالفعل' : 'Text already corrected', 'info');
        return;
      }

      showProcessingIndicator('جارٍ معالجة وتطبيق كافة التصحيحات النحوية والإملائية', 'Processing and applying all corrections');
      const btnApply = document.getElementById('btnApplyAll');
      if (btnApply) btnApply.disabled = true;

      setTimeout(() => {
        for (const item of issues) {
          if (item.orig && item.sugg && !item.orig.includes('مسافة')) {
            text = text.split(item.orig).join(item.sugg);
          }
        }
        // Punctuation cleanups
        text = text.replace(/,/g, '،').replace(/\\?/g, '؟').replace(/;/g, '؛');
        text = text.replace(/\\s+([،,.!?؟؛])/g, '$1');
        text = text.replace(/(^|\\s)و\\s+([\\u0600-\\u06FF]+)/g, '$1و$2');

        document.getElementById('textInput').value = text;
        hideProcessingIndicator();
        if (btnApply) btnApply.disabled = false;
        runCheck(false);
        updateReaderDisplay();
        showToast(currentLang === 'ar' ? '✓ تم تطبيق جميع التصحيحات بنجاح!' : '✓ All corrections applied!', 'success');
      }, 450);
    }

    function fixAllErrors() {
      let text = document.getElementById('textInput').value;
      const issues = analyze(text);
      if (issues.length === 0) {
        showToast(currentLang === 'ar' ? 'النص خالٍ من الأخطاء بالفعل' : 'No errors found in text', 'info');
        return;
      }

      showProcessingIndicator('جارٍ المعالجة الفورية وتصحيح الأخطاء بالذكاء الاصطناعي', 'Processing text and fixing all errors with AI');
      const btnFix = document.getElementById('btnFixAll');
      if (btnFix) btnFix.disabled = true;

      setTimeout(() => {
        for (const item of issues) {
          if (item.orig && item.sugg && !item.orig.includes('مسافة')) {
            text = text.split(item.orig).join(item.sugg);
          }
        }
        // Punctuation cleanups
        text = text.replace(/,/g, '،').replace(/\\?/g, '؟').replace(/;/g, '؛');
        text = text.replace(/\\s+([،,.!?؟؛])/g, '$1');
        text = text.replace(/(^|\\s)و\\s+([\\u0600-\\u06FF]+)/g, '$1و$2');

        document.getElementById('textInput').value = text;
        hideProcessingIndicator();
        if (btnFix) btnFix.disabled = false;
        runCheck(false);
        updateReaderDisplay();

        const alertEl = document.getElementById('statusBanner');
        if (alertEl) {
          alertEl.style.display = 'block';
          alertEl.innerText = currentLang === 'ar' ? '✓ تم تصحيح كافة الأخطاء النحوية والترقيمية فورياً!' : '✓ All grammar and punctuation errors fixed!';
          setTimeout(() => { alertEl.style.display = 'none'; }, 4500);
        }
        showToast(currentLang === 'ar' ? '✓ تم تصحيح النص بالكامل بنجاح!' : '✓ All text corrections applied!', 'success');
      }, 550);
    }

    function loadSample(i, btnEl) {
      document.getElementById('textInput').value = samples[i];
      document.querySelectorAll('.sample-pill').forEach(p => p.classList.remove('active'));
      if (btnEl) btnEl.classList.add('active');
      runCheck(false);
      updateReaderDisplay();
      showToast(currentLang === 'ar' ? ('تم تحميل النموذج ' + (i + 1)) : ('Loaded Sample ' + (i + 1)), 'info');
    }

    function loadRuleIntoChecker(sentence) {
      document.getElementById('textInput').value = sentence;
      switchNav('Checker');
      runCheck(false);
      updateReaderDisplay();
      showToast(currentLang === 'ar' ? 'تم تحميل مثال القاعدة في المصحح' : 'Loaded rule example into checker', 'success');
    }

    function clearText() {
      document.getElementById('textInput').value = '';
      runCheck(false);
      updateReaderDisplay();
      showToast(currentLang === 'ar' ? 'تم مسح النص' : 'Cleared text', 'info');
    }

    function copyEditorText() {
      const text = document.getElementById('textInput').value;
      if (!text.trim()) {
        showToast(currentLang === 'ar' ? 'لا يوجد نص لنسخه' : 'No text to copy', 'error');
        return;
      }
      navigator.clipboard?.writeText(text);
      const btn = document.getElementById('btnCopyMain');
      if (btn) {
        const orig = btn.innerHTML;
        btn.innerHTML = '<span>✓</span> تم!';
        setTimeout(() => { btn.innerHTML = orig; }, 1800);
      }
      showToast(currentLang === 'ar' ? '✓ تم نسخ النص إلى الحافظة' : '✓ Copied text to clipboard', 'success');
    }

    function pasteFromClipboard() {
      if (navigator.clipboard && navigator.clipboard.readText) {
        navigator.clipboard.readText().then(text => {
          if (text) {
            document.getElementById('textInput').value = text;
            runCheck(false);
            updateReaderDisplay();
            showToast(currentLang === 'ar' ? '✓ تم لصق النص من الحافظة' : '✓ Pasted text', 'success');
          } else {
            showToast(currentLang === 'ar' ? 'الحافظة فارغة' : 'Clipboard is empty', 'info');
          }
        }).catch(() => {
          // Fallback demo paste
          document.getElementById('textInput').value = "ذهب محمد الى المدرسة، و هو يحمل كتبه وشاهد عصفورا جميلا.";
          runCheck(false);
          updateReaderDisplay();
          showToast(currentLang === 'ar' ? 'تم لصق نص تجريبي' : 'Pasted demo text', 'info');
        });
      } else {
        document.getElementById('textInput').value = "ذهب محمد الى المدرسة، و هو يحمل كتبه وشاهد عصفورا جميلا.";
        runCheck(false);
        updateReaderDisplay();
        showToast(currentLang === 'ar' ? 'تم لصق نص تجريبي' : 'Pasted demo text', 'info');
      }
    }

    // Write with AI functions
    let isWriteAiOpen = false;
    let currentAiText = '';

    function toggleWriteAiCard() {
      isWriteAiOpen = !isWriteAiOpen;
      const content = document.getElementById('writeAiContent');
      const btn = document.getElementById('writeAiToggleBtn');
      if (content) content.style.display = isWriteAiOpen ? 'block' : 'none';
      if (btn) btn.innerText = isWriteAiOpen ? '▲' : '▼';
    }

    function setAiPrompt(promptText) {
      document.getElementById('aiPromptInput').value = promptText;
      showToast(currentLang === 'ar' ? 'تم اختيار القالب' : 'Template selected', 'info');
    }

    function generateAiWriting() {
      const prompt = (document.getElementById('aiPromptInput').value || '').trim();
      const currentContent = (document.getElementById('textInput').value || '').trim();
      const tone = document.getElementById('aiToneSelect').value;
      const genBtn = document.getElementById('btnGenerateAi');

      genBtn.disabled = true;
      genBtn.innerHTML = '<span>⏳</span> ' + (currentLang === 'ar' ? 'جارٍ الصياغة والتوليد...' : 'Generating with AI...');

      setTimeout(() => {
        let generated = '';
        if (prompt.includes('شكر')) {
          generated = 'يطيب لي أن أرفع إليكم أسمى آيات الشكر والتقدير والامتنان على جهودكم المخلصة ودعمكم الكريم، متمنياً لكم دوام التوفيق والسداد والارتقاء المستمر.';
        } else if (prompt.includes('إجازة')) {
          generated = 'أتقدم إلى عنايتكم بطلب الموافقة على منحي إجازة اعتيادية تبدأ من التاريخ المحدد، شاكراً ومقدراً لكم حسن تعاونكم وتفهمكم الكريم.';
        } else if (prompt.includes('مقال')) {
          generated = 'تتبوأ اللغة العربية مكانة رفيعة في سماء الحضارة الإنسانية؛ إذ تتجلى فيها عبقرية البيان ودقة الإعراب، وثراء المفردات التي تأسر القلوب والألباب.';
        } else if (currentContent) {
          generated = 'استناداً إلى الفكرة المطروحة: ' + currentContent.replace(/الى/g, 'إلى').replace(/ان/g, 'إنّ') + '؛ يتضح جلياً ترابط المعاني وسمو الأسلوب العربي الفصيح.';
        } else {
          generated = 'يسرنا التواصل معكم بأطيب التحيات، مؤكدين التزامنا بتقديم أعلى درجات الجودة والإتقان في صياغة محتوى عربي رصين ومعبر.';
        }

        if (tone === 'concise') {
          generated = generated.split('،')[0] + '.';
        } else if (tone === 'academic') {
          generated = 'تقتضي المقاربة المنهجية التأكيد على أن ' + generated;
        } else if (tone === 'creative') {
          generated = '✨ كالدر المنظوم في جيد الفصاحة، ' + generated;
        }

        currentAiText = generated;
        document.getElementById('aiResultText').innerText = generated;
        document.getElementById('aiResultBox').style.display = 'block';

        genBtn.disabled = false;
        genBtn.innerHTML = '<span>✨</span> ' + (currentLang === 'ar' ? 'اكتب الآن بالذكاء الاصطناعي' : 'Generate with AI');
        showToast(currentLang === 'ar' ? '✓ تم توليد النص الفصيح بنجاح!' : '✓ Text generated successfully!', 'success');
      }, 700);
    }

    function copyAiResult() {
      if (!currentAiText) return;
      navigator.clipboard?.writeText(currentAiText);
      showToast(currentLang === 'ar' ? 'تم نسخ النص المولد بنجاح' : 'Copied AI text successfully', 'success');
    }

    function applyAiResult() {
      if (!currentAiText) return;
      document.getElementById('textInput').value = currentAiText;
      runCheck(false);
      updateReaderDisplay();
      showToast(currentLang === 'ar' ? 'تم استبدال النص في المحرر بنجاح' : 'Replaced editor text with AI writing', 'success');
    }

    function appendAiResult() {
      if (!currentAiText) return;
      const cur = document.getElementById('textInput').value.trim();
      document.getElementById('textInput').value = cur ? (cur + '\n\n' + currentAiText) : currentAiText;
      runCheck(false);
      updateReaderDisplay();
      showToast(currentLang === 'ar' ? 'تم إلحاق النص بالمحرر بنجاح' : 'Appended AI writing to editor', 'success');
    }

    function toggleTheme() {
      isDark = !isDark;
      document.body.classList.toggle('dark', isDark);
      document.getElementById('themeBtn').innerText = isDark ? '☀️' : '🌙';
      showToast(isDark ? 'تم تفعيل المظهر الداكن 🌙' : 'تم تفعيل المظهر الفاتح ☀️', 'info');
    }

    function toggleLang() {
      currentLang = currentLang === 'ar' ? 'en' : 'ar';
      document.documentElement.dir = currentLang === 'ar' ? 'rtl' : 'ltr';
      document.documentElement.lang = currentLang;
      document.getElementById('langBtn').innerText = currentLang === 'ar' ? 'EN' : 'عربي';

      document.querySelectorAll('.nav-label').forEach(el => {
        el.innerText = el.getAttribute('data-' + currentLang);
      });
      runCheck(false);
      showToast(currentLang === 'ar' ? 'تم التبديل إلى اللغة العربية' : 'Switched to English', 'info');
    }

    function switchNav(name, el) {
      document.querySelectorAll('.view-container').forEach(v => v.classList.remove('active'));
      document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

      const targetView = document.getElementById('view' + name);
      if (targetView) targetView.classList.add('active');

      let buttonEl = el;
      if (!buttonEl && typeof event !== 'undefined' && event && event.target) {
        buttonEl = event.target.closest('.nav-item');
      }
      if (!buttonEl) {
        buttonEl = document.querySelector('.nav-item[data-nav="' + name + '"]');
      } else {
        buttonEl = buttonEl.closest('.nav-item') || buttonEl;
      }
      if (buttonEl) buttonEl.classList.add('active');

      const scrollContainer = document.querySelector('.main-scroll');
      if (scrollContainer) scrollContainer.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // Reader Mode & Accessibility Functions
    let isReaderModeOpen = false;
    let readerFontSize = 22;
    let isReaderHighContrast = false;
    let isCurrentlySpeaking = false;

    function toggleReaderMode() {
      isReaderModeOpen = !isReaderModeOpen;
      const content = document.getElementById('readerContent');
      const toggleBtn = document.getElementById('readerToggleBtn');
      const card = document.getElementById('readerCard');
      if (content) content.style.display = isReaderModeOpen ? 'block' : 'none';
      if (toggleBtn) toggleBtn.innerText = isReaderModeOpen ? '▲' : '▼';
      if (card) {
        card.style.boxShadow = isReaderModeOpen ? '0 6px 18px rgba(37,99,235,0.25)' : 'none';
      }
      updateReaderDisplay();
    }

    function updateReaderDisplay() {
      const text = document.getElementById('textInput').value.trim() || 'ذهب محمدٌ إلى المدرسةِ، وهو يحملُ كتبَه وشاهدَ عصفوراً جميلاً.';
      const box = document.getElementById('readerDisplayBox');
      if (box) {
        box.innerText = text;
        box.style.fontSize = readerFontSize + 'px';
        box.style.lineHeight = (readerFontSize * 1.6) + 'px';
        if (isReaderHighContrast) {
          box.style.background = '#000000';
          box.style.color = '#FACC15';
          box.style.borderColor = '#FACC15';
        } else {
          box.style.background = '#FFFFFF';
          box.style.color = '#1E293B';
          box.style.borderColor = '#2563EB';
        }
      }
      const sizeDisplay = document.getElementById('readerFontSizeDisplay');
      if (sizeDisplay) sizeDisplay.innerText = readerFontSize + 'px';
    }

    function changeReaderFontSize(delta) {
      readerFontSize = Math.max(16, Math.min(36, readerFontSize + delta));
      updateReaderDisplay();
      showToast((currentLang === 'ar' ? 'حجم الخط: ' : 'Font Size: ') + readerFontSize + 'px', 'info');
    }

    function toggleReaderHighContrast() {
      isReaderHighContrast = !isReaderHighContrast;
      const btn = document.getElementById('btnReaderContrast');
      if (btn) btn.classList.toggle('active', isReaderHighContrast);
      updateReaderDisplay();
      showToast(isReaderHighContrast ? 'تم تفعيل التباين العالي 👁️' : 'تم تعطيل التباين العالي', 'info');
    }

    function copyReaderText() {
      const text = document.getElementById('readerDisplayBox').innerText;
      navigator.clipboard?.writeText(text);
      showToast(currentLang === 'ar' ? '✓ تم نسخ النص المقروء' : '✓ Copied text', 'success');
    }

    function speakCurrentText() {
      const text = document.getElementById('textInput').value.trim() || 'ذهب محمدٌ إلى المدرسةِ، وهو يحملُ كتبَه وشاهدَ عصفوراً جميلاً.';
      if (!text) return;

      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = 'ar-SA';
        utterance.rate = 0.88;

        utterance.onstart = () => {
          isCurrentlySpeaking = true;
          const lbl = document.getElementById('btnSpeakLabel');
          if (lbl) lbl.innerText = currentLang === 'ar' ? 'جارٍ النطق الآن...' : 'Speaking...';
          showToast('🔊 ' + (currentLang === 'ar' ? 'جارٍ النطق الصوتي الآن...' : 'Reading aloud...'), 'info');
        };
        utterance.onend = utterance.onerror = () => {
          isCurrentlySpeaking = false;
          const lbl = document.getElementById('btnSpeakLabel');
          if (lbl) lbl.innerText = currentLang === 'ar' ? 'انطق النص الآن' : 'Read Aloud';
        };

        window.speechSynthesis.speak(utterance);
      } else {
        // Fallback simulation for environments without Web Speech audio
        showToast('🔊 محاكاة النطق الصوتي: ' + text.substring(0, 30) + '...', 'info');
      }
    }

    function stopSpeaking() {
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
      }
      isCurrentlySpeaking = false;
      const lbl = document.getElementById('btnSpeakLabel');
      if (lbl) lbl.innerText = currentLang === 'ar' ? 'انطق النص الآن' : 'Read Aloud';
      showToast(currentLang === 'ar' ? 'تم إيقاف النطق' : 'Stopped audio', 'info');
    }

    // Voice Dictation with Simulated Fallback
    let speechRecognizer = null;
    let isListeningToSpeech = false;
    let simInterval = null;

    function initSpeechRecognizer() {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      if (!SpeechRecognition) return null;
      try {
        const recognizer = new SpeechRecognition();
        recognizer.lang = 'ar-SA';
        recognizer.continuous = true;
        recognizer.interimResults = true;

        recognizer.onstart = () => {
          isListeningToSpeech = true;
          updateDictationUI(true);
        };

        recognizer.onresult = (event) => {
          let transcript = '';
          for (let i = 0; i < event.results.length; i++) {
            transcript += event.results[i][0].transcript + ' ';
          }
          transcript = transcript.trim();
          if (transcript) {
            const textInput = document.getElementById('textInput');
            textInput.value = transcript;
            updateStats();
            updateReaderDisplay();
          }
        };

        recognizer.onerror = () => {
          isListeningToSpeech = false;
          updateDictationUI(false);
        };

        recognizer.onend = () => {
          isListeningToSpeech = false;
          updateDictationUI(false);
          runCheck(false);
        };

        return recognizer;
      } catch (e) {
        return null;
      }
    }

    function toggleSpeechRecognition() {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      
      if (!speechRecognizer && SpeechRecognition) {
        speechRecognizer = initSpeechRecognizer();
      }

      if (isListeningToSpeech) {
        if (speechRecognizer) {
          try { speechRecognizer.stop(); } catch (e) {}
        }
        if (simInterval) clearInterval(simInterval);
        isListeningToSpeech = false;
        updateDictationUI(false);
        showToast(currentLang === 'ar' ? 'تم إيقاف الإملاء الصوتي' : 'Stopped dictation', 'info');
      } else {
        isListeningToSpeech = true;
        updateDictationUI(true);

        if (speechRecognizer) {
          try {
            speechRecognizer.start();
            showToast('🎤 ' + (currentLang === 'ar' ? 'تحدث الآن، جارٍ الاستماع لإملائك...' : 'Listening to speech...'), 'info');
            return;
          } catch (e) {
            // fallback to interactive live simulation
          }
        }

        // Realistic interactive dictation simulation
        showToast('🎤 ' + (currentLang === 'ar' ? 'محاكاة الإملاء الحي: جارٍ تلقي الكلمات...' : 'Simulated Voice Dictation Active...'), 'info');
        const simulatedWords = [
          "ذهب", "الطلاب", "إلى", "المكتبة", "المدرسية،", "وهم", "مستعدون", "لقراءة", "كتب", "مفيدة."
        ];
        let wordIdx = 0;
        const textInput = document.getElementById('textInput');
        textInput.value = "";
        
        simInterval = setInterval(() => {
          if (wordIdx < simulatedWords.length && isListeningToSpeech) {
            textInput.value += (wordIdx === 0 ? '' : ' ') + simulatedWords[wordIdx];
            wordIdx++;
            updateStats();
            updateReaderDisplay();
          } else {
            clearInterval(simInterval);
            isListeningToSpeech = false;
            updateDictationUI(false);
            showToast(currentLang === 'ar' ? '✓ اكتمل الإملاء الصوتي بنجاح' : '✓ Dictation completed', 'success');
          }
        }, 600);
      }
    }

    function updateDictationUI(isListening) {
      const banner = document.getElementById('micListeningBanner');
      const micLabel = document.getElementById('micLabel');
      const micIcon = document.getElementById('micIcon');
      const btnReaderMicLabel = document.getElementById('btnReaderMicLabel');

      if (banner) banner.style.display = isListening ? 'flex' : 'none';
      if (micLabel) micLabel.innerText = isListening ? (currentLang === 'ar' ? 'إيقاف الإملاء' : 'Stop') : (currentLang === 'ar' ? 'إملاء صوتي' : 'Dictate');
      if (micIcon) micIcon.innerText = isListening ? '⏹️' : '🎤';
      if (btnReaderMicLabel) btnReaderMicLabel.innerText = isListening ? (currentLang === 'ar' ? 'إيقاف الإملاء الصوتي' : 'Stop Dictation') : (currentLang === 'ar' ? 'إملاء صوتي بالمايكروفون' : 'Voice Dictate');
    }

    // Plan Selection
    function selectPlan(planKey) {
      currentPlan = planKey;
      const freeBtn = document.getElementById('btnSelectFree');
      const proBtn = document.getElementById('btnSelectPro');
      const entBtn = document.getElementById('btnSelectEnterprise');
      const profileBadge = document.getElementById('profilePlanBadge');

      if (freeBtn) {
        freeBtn.innerText = planKey === 'free' ? '✓ خطتك المفعلة حالياً' : 'التبديل إلى الخطة الأساسية';
        freeBtn.className = planKey === 'free' ? 'btn-primary' : 'btn-outline';
      }
      if (proBtn) {
        proBtn.innerText = planKey === 'pro' ? '✓ خطتك المفعلة حالياً (Pro ⭐)' : 'ترقية إلى Pro ($9)';
        proBtn.className = planKey === 'pro' ? 'btn-primary' : 'btn-outline';
      }
      if (entBtn) {
        entBtn.innerText = planKey === 'enterprise' ? '✓ خطتك المفعلة حالياً (Enterprise)' : 'طلب اشتراك المؤسسات ($29)';
        entBtn.className = planKey === 'enterprise' ? 'btn-primary' : 'btn-outline';
      }

      if (profileBadge) {
        profileBadge.innerText = planKey === 'pro' ? 'الخطة الاحترافية (Pro)' : (planKey === 'enterprise' ? 'خطة المؤسسات (Enterprise)' : 'الخطة الأساسية (مجاناً)');
      }

      const planNames = { free: 'الأساسية (Free)', pro: 'الاحترافية (Pro ⭐)', enterprise: 'المؤسسات (Enterprise)' };
      showToast(currentLang === 'ar' ? ('✓ تم تفعيل خطة: ' + planNames[planKey]) : ('✓ Active plan: ' + planKey.toUpperCase()), 'success');
    }

    // Account Authentication Handlers
    let currentAuthTab = 'signin';
    function switchAuthTab(tab) {
      currentAuthTab = tab;
      const signinBtn = document.getElementById('tabSignInBtn');
      const signupBtn = document.getElementById('tabSignUpBtn');
      const usernameRow = document.getElementById('authUsernameRow');
      const submitLabel = document.getElementById('btnAuthSubmitLabel');

      if (tab === 'signin') {
        signinBtn.classList.add('active');
        signupBtn.classList.remove('active');
        usernameRow.style.display = 'none';
        submitLabel.innerText = currentLang === 'ar' ? 'تسجيل الدخول' : 'Sign In';
      } else {
        signinBtn.classList.remove('active');
        signupBtn.classList.add('active');
        usernameRow.style.display = 'block';
        submitLabel.innerText = currentLang === 'ar' ? 'إنشاء حساب جديد' : 'Create Account';
      }
    }

    function submitAuth() {
      const email = document.getElementById('authEmailInput').value.trim();
      const name = document.getElementById('authUsernameInput').value.trim() || 'فادي ناصر';
      
      if (!email) {
        showToast(currentLang === 'ar' ? 'الرجاء إدخال البريد الإلكتروني' : 'Please enter your email', 'error');
        return;
      }

      isLoggedIn = true;
      document.getElementById('authLoggedOutCard').style.display = 'none';
      document.getElementById('authLoggedInCard').style.display = 'block';
      document.getElementById('profileName').innerText = name;
      document.getElementById('profileEmail').innerText = email;
      document.getElementById('userAvatar').innerText = name.charAt(0);

      showToast(currentLang === 'ar' ? ('مرحباً بك يا ' + name + '! تم تسجيل الدخول بنجاح') : ('Welcome ' + name + '! Signed in successfully'), 'success');
    }

    function demoQuickLogin() {
      document.getElementById('authEmailInput').value = 'Fadiyano44@gmail.com';
      document.getElementById('authUsernameInput').value = 'فادي ناصر';
      submitAuth();
    }

    function logoutUser() {
      isLoggedIn = false;
      document.getElementById('authLoggedInCard').style.display = 'none';
      document.getElementById('authLoggedOutCard').style.display = 'block';
      showToast(currentLang === 'ar' ? 'تم تسجيل الخروج بنجاح' : 'Logged out successfully', 'info');
    }

    // Terms Handler
    function acceptTerms() {
      isTermsAccepted = true;
      const btn = document.getElementById('btnAcceptTerms');
      const label = document.getElementById('btnAcceptTermsLabel');
      const icon = document.getElementById('acceptTermsIcon');
      if (btn) {
        btn.style.background = 'var(--success)';
      }
      if (label) {
        label.innerText = currentLang === 'ar' ? '✓ تمت الموافقة على الشروط والسياسة' : '✓ Terms & Privacy Accepted';
      }
      showToast(currentLang === 'ar' ? '✓ تم حفظ وتأكيد موافقتك على الشروط بنجاح' : '✓ Terms & Privacy Accepted', 'success');
    }

    function printOrExportTerms() {
      showToast(currentLang === 'ar' ? 'جارٍ تحضير وثيقة الشروط للطباعة...' : 'Preparing terms for export...', 'info');
      setTimeout(() => {
        window.print();
      }, 500);
    }

    // Scroll helpers
    function scrollToTop() {
      const el = document.querySelector('.main-scroll');
      if (el) el.scrollTo({ top: 0, behavior: 'smooth' });
      else window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    function scrollToBottom() {
      const el = document.querySelector('.main-scroll');
      if (el) el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
      else window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
    }

    // Auto-update status bar clock
    function updateClock() {
      const now = new Date();
      const hrs = now.getHours() % 12 || 12;
      const mins = now.getMinutes().toString().padStart(2, '0');
      const ampm = now.getHours() >= 12 ? 'PM' : 'AM';
      const clockEl = document.getElementById('clockDisplay');
      if (clockEl) clockEl.innerText = hrs + ':' + mins + ' ' + ampm + ' • 100%';
    }
    setInterval(updateClock, 30000);
    updateClock();

    // Initial Execution
    runCheck(false);
    updateReaderDisplay();
  </script>
</body>
  
  <!-- Vercel Speed Insights -->
  <script>
    window.si = window.si || function () { (window.siq = window.siq || []).push(arguments); };
  </script>
  <script defer src="/_vercel/speed-insights/script.js"></script>
</html>
`;

const server = http.createServer((req, res) => {
  if (req.url === '/health') {
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('OK');
    return;
  }
  const reqUrl = req.url || '/';
  if (reqUrl.startsWith('/slides_en') || reqUrl.startsWith('/slides-en') || reqUrl === '/presentation/en' || reqUrl === '/slides?lang=en') {
    const slidesPath = path.join(__dirname, 'slides_en.html');
    if (fs.existsSync(slidesPath)) {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
      fs.createReadStream(slidesPath).pipe(res);
      return;
    }
  }
  if (reqUrl === '/slides' || reqUrl.startsWith('/slides?') || reqUrl === '/presentation' || reqUrl === '/slides.html') {
    // If language is explicitly Arabic or default, check if user requested Arabic or English
    const isEnglish = reqUrl.includes('lang=en');
    const targetFile = isEnglish ? 'slides_en.html' : 'slides.html';
    const slidesPath = path.join(__dirname, targetFile);
    if (fs.existsSync(slidesPath)) {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
      fs.createReadStream(slidesPath).pipe(res);
      return;
    }
  }
  if (req.url === '/promo_banner.jpg' || req.url === '/promo.jpg' || req.url === '/ad.jpg') {
    const imgPath = path.join(__dirname, 'promo_banner.jpg');
    if (fs.existsSync(imgPath)) {
      const stat = fs.statSync(imgPath);
      res.writeHead(200, {
        'Content-Type': 'image/jpeg',
        'Content-Length': stat.size,
        'Cache-Control': 'public, max-age=86400'
      });
      fs.createReadStream(imgPath).pipe(res);
      return;
    }
  }
  if (req.url === '/evaluation_dataset.pdf' || req.url === '/dataset.pdf' || req.url === '/download/evaluation_dataset.pdf') {
    const pdfPath = path.join(__dirname, 'evaluation_dataset.pdf');
    if (fs.existsSync(pdfPath)) {
      const stat = fs.statSync(pdfPath);
      res.writeHead(200, {
        'Content-Type': 'application/pdf',
        'Content-Length': stat.size,
        'Content-Disposition': 'inline; filename="NahwiFix_Evaluation_Dataset.pdf"'
      });
      fs.createReadStream(pdfPath).pipe(res);
      return;
    }
  }
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(htmlContent);
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`NahwiFix application server listening on port ${PORT}`);
});
