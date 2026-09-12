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
      --primary-container: #A7F3D0;
      --on-primary-container: #00201A;
      --secondary: #0D47A1;
      --surface: #FFFFFF;
      --bg: #F8FAF9;
      --text: #191C1B;
      --text-muted: #64748B;
      --border: #E2E8F0;
      --error: #DC2626;
      --error-bg: #FEE2E2;
      --success: #059669;
      --grammar: #2563EB;
      --punct: #7C3AED;
      --spelling: #EA580C;
    }
    body.dark {
      --primary: #34D399;
      --primary-container: #005144;
      --on-primary-container: #A7F3D0;
      --secondary: #93C5FD;
      --surface: #1E293B;
      --bg: #0F172A;
      --text: #F1F5F9;
      --text-muted: #94A3B8;
      --border: #334155;
      --error: #F87171;
      --error-bg: #7F1D1D;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Cairo', sans-serif; }
    body { background: var(--bg); color: var(--text); display: flex; justify-content: center; min-height: 100vh; padding: 16px; }
    .device-frame {
      width: 100%;
      max-width: 480px;
      background: var(--surface);
      border-radius: 28px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.12);
      border: 1px solid var(--border);
      display: flex;
      flex-direction: column;
      overflow: hidden;
      min-height: 850px;
    }
    .status-bar {
      padding: 10px 18px 4px;
      display: flex;
      justify-content: space-between;
      font-size: 11px;
      font-weight: 700;
      color: var(--text-muted);
    }
    header {
      padding: 12px 18px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid var(--border);
      background: var(--surface);
    }
    .logo-badge { display: flex; align-items: center; gap: 8px; }
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
    }
    .header-actions { display: flex; gap: 8px; }
    .icon-btn {
      background: var(--border);
      border: none;
      color: var(--text);
      padding: 6px 12px;
      border-radius: 8px;
      cursor: pointer;
      font-weight: 600;
      font-size: 12px;
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
    }
    .hero-card h2 { font-size: 17px; margin-bottom: 4px; }
    .hero-card p { font-size: 12px; opacity: 0.9; }
    .samples { display: flex; gap: 6px; flex-wrap: wrap; margin-top: 10px; }
    .sample-pill {
      background: rgba(255,255,255,0.7);
      color: #000;
      border: none;
      padding: 4px 10px;
      border-radius: 20px;
      font-size: 11px;
      cursor: pointer;
      font-weight: 600;
    }
    .editor-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 16px;
      padding: 14px;
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    textarea {
      width: 100%;
      height: 110px;
      border: 1px solid var(--border);
      border-radius: 10px;
      padding: 10px;
      font-size: 14px;
      background: var(--bg);
      color: var(--text);
      resize: none;
      outline: none;
    }
    textarea:focus { border-color: var(--primary); }
    .stats-bar {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      background: var(--bg);
      padding: 6px 12px;
      border-radius: 8px;
      color: var(--text-muted);
      font-weight: 600;
    }
    .action-row { display: flex; gap: 8px; }
    .btn-primary {
      flex: 1;
      background: var(--primary);
      color: #fff;
      border: none;
      padding: 10px;
      border-radius: 10px;
      font-weight: 700;
      font-size: 13px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
    }
    .btn-secondary {
      flex: 1;
      background: var(--secondary);
      color: #fff;
      border: none;
      padding: 10px;
      border-radius: 10px;
      font-weight: 700;
      font-size: 13px;
      cursor: pointer;
    }
    .issues-chips { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 6px; }
    .chip {
      background: var(--bg);
      border: 1px solid var(--border);
      padding: 6px 10px;
      border-radius: 8px;
      font-size: 12px;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
    .chip.active { border-color: var(--primary); background: var(--primary-container); color: var(--on-primary-container); }
    .inspector-card {
      background: var(--bg);
      border: 1px solid var(--border);
      border-radius: 14px;
      padding: 14px;
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
      cursor: pointer;
      font-size: 11px;
      font-weight: 600;
    }
    .nav-item.active { color: var(--primary); }
    .view-container { display: none; }
    .view-container.active { display: block; }
    .plan-card {
      border: 1px solid var(--border);
      padding: 14px;
      border-radius: 12px;
      margin-bottom: 10px;
      background: var(--surface);
    }
    .plan-card.pro { border-color: var(--primary); background: var(--primary-container); color: var(--on-primary-container); }
  </style>
</head>
<body>
  <div class="device-frame">
    <div class="status-bar">
      <span>NahwiFix Android</span>
      <span>9:41 AM • 100%</span>
    </div>
    <header>
      <div class="logo-badge">
        <div class="logo-icon">ن</div>
        <strong>NahwiFix</strong>
      </div>
      <div class="header-actions">
        <a href="/evaluation_dataset.pdf" target="_blank" download="NahwiFix_Evaluation_Dataset.pdf" class="icon-btn" title="تحميل ملف بيانات التقييم (PDF) / Download Evaluation Dataset" style="text-decoration:none; display:flex; align-items:center; justify-content:center; font-size:14px; font-weight:bold; color:inherit;">📄 PDF</a>
        <button class="icon-btn" onclick="toggleReaderMode()" id="readerBtn" title="وضع القارئ الميسر">📖</button>
        <button class="icon-btn" onclick="toggleLang()" id="langBtn">EN</button>
        <button class="icon-btn" onclick="toggleTheme()" id="themeBtn">🌙</button>
      </div>
    </header>

    <div class="main-scroll">
      <!-- CHECKER VIEW -->
      <div id="viewChecker" class="view-container active">
        <div class="hero-card">
          <h2 id="heroTitle">مصحح النحو والترقيم العربي</h2>
          <p id="heroSub">افحص النصوص العربية وصحح الهمزات، علامات الترقيم، ومطابقة الفعل والفاعل فورياً.</p>
          <div class="samples">
            <button class="sample-pill" onclick="loadSample(0)">مثال 1: ذهب محمد...</button>
            <button class="sample-pill" onclick="loadSample(1)">مثال 2: إن المعلمة...</button>
            <button class="sample-pill" onclick="loadSample(2)">مثال 3: في الحديقة...</button>
          </div>
        </div>

        <!-- ACCESSIBLE READER MODE SECTION (وضع القارئ الصوتي الميسر لمن لا يجيد القراءة) -->
        <div class="editor-card" id="readerCard" style="margin-top: 14px; border: 2px solid #2563EB; background: #EFF6FF;">
          <div style="display:flex; justify-content:space-between; align-items:center; cursor:pointer;" onclick="toggleReaderMode()">
            <div style="display:flex; align-items:center; gap:8px;">
              <span style="background:#2563EB; color:#fff; padding:6px 10px; border-radius:50%; font-size:16px;">🔊</span>
              <div>
                <strong style="color:#1E40AF;" id="readerCardTitle">وضع القارئ الصوتي الميسّر (Accessibility)</strong>
                <div style="font-size:12px; color:#3B82F6;" id="readerCardSub">خدمة نطق النصوص المكتوبة وتكبير الخط لغير القادرين على القراءة</div>
              </div>
            </div>
            <button class="icon-btn" id="readerToggleBtn">▼</button>
          </div>

          <div id="readerContent" style="display:none; margin-top:14px;">
            <div style="display:flex; gap:10px; margin-bottom:12px; flex-wrap:wrap; align-items:center;">
              <button class="btn-primary" onclick="toggleSpeechRecognition()" id="btnReaderMic" style="background:#DC2626; padding:8px 16px;">
                <span>🎤</span> <span id="btnReaderMicLabel">إملاء صوتي بالمايكروفون</span>
              </button>
              <button class="btn-primary" onclick="speakCurrentText()" id="btnSpeakText" style="background:#2563EB; padding:8px 16px;">
                <span>🔊</span> <span id="btnSpeakLabel">انطق النص الآن</span>
              </button>
              <button class="btn-secondary" onclick="stopSpeaking()" id="btnStopSpeech" style="padding:8px 14px; color:#DC2626; border-color:#DC2626;">
                <span>⏹️</span> إيقاف النطق
              </button>
              <div style="display:flex; align-items:center; gap:6px; margin-right:auto;">
                <span style="font-size:12px; font-weight:bold;">حجم الخط:</span>
                <button class="sample-pill" onclick="changeReaderFontSize(-2)">A-</button>
                <span id="readerFontSizeDisplay" style="font-weight:bold; font-size:13px;">22px</span>
                <button class="sample-pill" onclick="changeReaderFontSize(2)">A+</button>
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
              <span style="background:var(--primary-container); color:var(--primary); padding:6px; border-radius:50%;">✨</span>
              <div>
                <strong style="color:var(--primary);">الكتابة بالذكاء الاصطناعي (Write with AI)</strong>
                <div style="font-size:12px; color:var(--on-surface-variant);">صياغة نصوص فصيحة، رسائل، ومقالات بالذكاء الاصطناعي</div>
              </div>
            </div>
            <button class="icon-btn" id="writeAiToggleBtn">▼</button>
          </div>

          <div id="writeAiContent" style="display:none; margin-top:14px;">
            <div style="font-size:12px; font-weight:bold; margin-bottom:6px; color:var(--on-surface-variant);">نماذج وقوالب جاهزة:</div>
            <div style="display:flex; flex-wrap:wrap; gap:6px; margin-bottom:10px;">
              <button class="sample-pill" onclick="setAiPrompt('اكتب رسالة شكر وتقدير رسمية موجهة لمدير العمل أو الزملاء بأسلوب فصيح ومؤثر.')">رسالة شكر رسمية</button>
              <button class="sample-pill" onclick="setAiPrompt('صياغة طلب إجازة رسمي لجهة العمل مع خالص الاحترام والتقدير.')">طلب إجازة رسمي</button>
              <button class="sample-pill" onclick="setAiPrompt('اكتب مقدمة مقال رصين وجذاب عن مكانة اللغة العربية وجماليات النحو والبلاغة.')">مقدمة مقال أدبي</button>
              <button class="sample-pill" onclick="setAiPrompt('أعد صياغة النص المكتوب في المحرر بأسلوب عربي فصيح ورصين ومترابط.')">إعادة صياغة النص</button>
            </div>

            <textarea id="aiPromptInput" rows="2" style="width:100%; border-radius:8px; border:1px solid #ccc; padding:8px; font-family:inherit; font-size:14px; margin-bottom:10px; resize:vertical;" placeholder="ماذا تريد أن يكتب الذكاء الاصطناعي؟ (مثال: خطاب تهنئة، بريد رسمي...)"></textarea>

            <div style="display:flex; gap:10px; align-items:center; margin-bottom:12px; flex-wrap:wrap;">
              <label style="font-size:12px; font-weight:bold; color:var(--on-surface-variant);">الأسلوب / النبرة:</label>
              <select id="aiToneSelect" style="padding:6px 10px; border-radius:8px; border:1px solid #ccc; font-family:inherit;">
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

            <div id="aiResultBox" style="display:none; background:var(--primary-container); padding:12px; border-radius:10px; margin-top:10px;">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
                <strong style="color:var(--primary); font-size:13px;">✓ النص المكتوب بالذكاء الاصطناعي:</strong>
                <button class="icon-btn" onclick="copyAiResult()" style="padding:2px 8px; font-size:12px;">نسخ</button>
              </div>
              <div id="aiResultText" style="line-height:1.7; font-size:14px; margin-bottom:10px; color:var(--on-surface);"></div>
              <div style="display:flex; gap:8px;">
                <button class="btn-primary" onclick="applyAiResult()" style="padding:6px 12px; font-size:12px;">استبدال بالمحرر</button>
                <button class="btn-secondary" onclick="appendAiResult()" style="padding:6px 12px; font-size:12px;">إلحاق بالمحرر</button>
              </div>
            </div>
          </div>
        </div>

        <div class="editor-card" style="margin-top: 14px;">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div style="display:flex; align-items:center; gap:8px;">
              <strong id="inputLabel">النص العربي</strong>
              <button class="sample-pill" onclick="toggleSpeechRecognition()" id="btnMicInput" style="display:flex; align-items:center; gap:4px; font-weight:bold; color:#DC2626; border-color:#DC2626; padding:3px 10px;">
                <span id="micIcon">🎤</span> <span id="micLabel">إملاء صوتي</span>
              </button>
            </div>
            <button class="icon-btn" onclick="clearText()" style="padding: 2px 8px;">مسح</button>
          </div>
          <div id="micListeningBanner" style="display:none; background:#FEF2F2; border:1px solid #DC2626; color:#991B1B; padding:8px 12px; border-radius:8px; margin-top:8px; font-size:12px; font-weight:bold; display:none; align-items:center; justify-content:space-between;">
            <div style="display:flex; align-items:center; gap:6px;">
              <span class="pulsing-dot" style="display:inline-block; width:8px; height:8px; border-radius:50%; background:#DC2626;"></span>
              <span id="micStatusText">جارٍ الاستماع لإملائك الصوتي باللغة العربية الآن... تحدث بوضوح</span>
            </div>
            <button class="sample-pill" onclick="toggleSpeechRecognition()" style="padding:2px 8px; font-size:11px; background:#DC2626; color:#fff; border:none;">إيقاف</button>
          </div>
          <textarea id="textInput" placeholder="تحدث معي بالمايكروفون أو اكتب نصك هنا...">ذهب محمد الى المدرسة، و هو يحمل كتبه وشاهد عصفورا جميلا.</textarea>
          <div class="stats-bar">
            <span id="statWords">10 كلمات</span>
            <span id="statChars">54 أحرف</span>
            <span id="statIssues" style="color:var(--error)">5 ملاحظات</span>
          </div>
          <div id="statusBanner" style="display:none; background:var(--primary-container); color:var(--on-primary-container); padding:10px 14px; border-radius:10px; margin-bottom:10px; font-weight:bold; font-size:13px; text-align:center;">
            ✓ تم تصحيح كافة الأخطاء بنجاح!
          </div>
          <div class="action-row">
            <button class="btn-primary" onclick="fixAllErrors()" id="btnFixAll" style="background:var(--primary);">
              <span>✨</span> <span id="btnFixErrorsLabel">تصحيح الأخطاء</span>
            </button>
            <button class="btn-secondary" onclick="runCheck()" id="btnCheck">تدقيق وفحص</button>
            <button class="btn-secondary" onclick="applyAll()" id="btnApplyAll">تطبيق الكل</button>
          </div>
        </div>

        <div style="margin-top: 14px;">
          <h4 id="issuesHeading">الأخطاء المكتشفة:</h4>
          <div id="issuesList" class="issues-chips"></div>
        </div>

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
          <p id="inspExplanation" style="font-size:12px; color:var(--text-muted); margin-bottom: 10px;">
            حرف جر يجب كتابته بهمز قطع مكسورة تحت الألف.
          </p>
          <button class="btn-primary" onclick="applySingle()" id="btnApplySingle" style="width:100%">تطبيق هذا التصحيح</button>
        </div>
      </div>

      <!-- RULES VIEW -->
      <div id="viewRules" class="view-container">
        <h3 style="margin-bottom: 8px;">دليل القواعد النحوية</h3>
        <div class="plan-card">
          <strong>همزتا الوصل والقطع</strong>
          <p style="font-size:12px; color:var(--text-muted); margin: 4px 0;">همزة القطع تثبت نطقاً ورسماً (إلى، أرسل). همزة الوصل تسقط في درج الكلام (استمع، اكتب).</p>
          <div style="font-size:12px; color:var(--success); font-weight:bold;">الصواب: ذهب محمدٌ إلى المدرسةِ</div>
        </div>
        <div class="plan-card">
          <strong>مطابقة الفعل للفاعل</strong>
          <p style="font-size:12px; color:var(--text-muted); margin: 4px 0;">يؤنث الفعل مع الفاعل المؤنث الحقيقي.</p>
          <div style="font-size:12px; color:var(--success); font-weight:bold;">الصواب: المعلمةُ تُساعدُ الطلاب</div>
        </div>
        <div class="plan-card">
          <strong>رسم تنوين الفتح</strong>
          <p style="font-size:12px; color:var(--text-muted); margin: 4px 0;">يُزاد ألف بعد تنوين الفتح في الأسماء المنونة (عصفوراً، جميلاً).</p>
          <div style="font-size:12px; color:var(--success); font-weight:bold;">الصواب: شاهد عصفوراً جميلاً</div>
        </div>
        <div class="plan-card">
          <strong>علامات الترقيم العربية</strong>
          <p style="font-size:12px; color:var(--text-muted); margin: 4px 0;">الفاصلة العربية (،) وعلامة الاستفهام (؟) تتجه لليمين وتلتصق بالكلمة السابقة.</p>
          <div style="font-size:12px; color:var(--success); font-weight:bold;">الصواب: نعم، شكراً لك.</div>
        </div>
      </div>

      <!-- PRICING VIEW -->
      <div id="viewPricing" class="view-container">
        <h3 style="margin-bottom: 8px;">خطط وأسعار NahwiFix</h3>
        <div class="plan-card">
          <h4>الخطة الأساسية</h4>
          <div style="font-size:18px; font-weight:bold; margin: 4px 0;">مجاناً دائماً</div>
          <p style="font-size:12px; color:var(--text-muted)">1,000 كلمة لكل فحص، قواعد النحو والترقيم الأساسية.</p>
        </div>
        <div class="plan-card pro">
          <h4>الخطة الاحترافية (Pro) ⭐</h4>
          <div style="font-size:18px; font-weight:bold; margin: 4px 0;">$9 / شهرياً</div>
          <p style="font-size:12px;">فحص غير محدود، تشكيل بالحركات، تعرف بصري OCR، وتوليد نطق صوتي.</p>
          <button class="btn-primary" style="margin-top:8px; width:100%" onclick="alert('تم اختيار الخطة الاحترافية')">ترقية الآن</button>
        </div>
        <div class="plan-card">
          <h4>خطة المؤسسات (Enterprise)</h4>
          <div style="font-size:18px; font-weight:bold; margin: 4px 0;">$29 / شهرياً</div>
          <p style="font-size:12px; color:var(--text-muted)">دعم فني مخصص، تراخيص متعددة، والوصول لواجهة البرمجة (API).</p>
        </div>
      </div>

      <!-- ACCOUNT VIEW -->
      <div id="viewAccount" class="view-container">
        <h3 style="margin-bottom: 8px;">حساب NahwiFix</h3>
        <div class="editor-card">
          <input type="text" placeholder="اسم المستخدم" style="padding:10px; border:1px solid var(--border); border-radius:8px; margin-bottom:8px;">
          <input type="email" placeholder="البريد الإلكتروني" style="padding:10px; border:1px solid var(--border); border-radius:8px; margin-bottom:8px;">
          <input type="password" placeholder="كلمة المرور" style="padding:10px; border:1px solid var(--border); border-radius:8px; margin-bottom:12px;">
          <button class="btn-primary" onclick="alert('تم تسجيل الدخول بنجاح')">تسجيل الدخول</button>
        </div>
      </div>

      <!-- TERMS VIEW -->
      <div id="viewTerms" class="view-container">
        <h3 style="margin-bottom: 8px;">الشروط والخصوصية</h3>
        <div class="editor-card" style="font-size:13px; line-height:1.6;">
          <p><strong>الخصوصية:</strong> جميع النصوص تفحص محلياً على جهازك ويتم حفظ السجل في قاعدة بيانات Room المشفرة لضمان الخصوصية التامة.</p>
          <p style="margin-top:8px;"><strong>الملكية الفكرية:</strong> جميع حقوق النصوص المحررة تعود ملكيتها للمستخدم.</p>
        </div>
      </div>
    </div>

    <!-- BOTTOM NAVIGATION -->
    <nav class="bottom-nav">
      <button class="nav-item active" onclick="switchNav('Checker')">
        <span>📝</span>
        <span class="nav-label" data-ar="المصحح" data-en="Checker">المصحح</span>
      </button>
      <button class="nav-item" onclick="switchNav('Rules')">
        <span>📖</span>
        <span class="nav-label" data-ar="القواعد" data-en="Rules">القواعد</span>
      </button>
      <button class="nav-item" onclick="switchNav('Pricing')">
        <span>💳</span>
        <span class="nav-label" data-ar="الأسعار" data-en="Pricing">الأسعار</span>
      </button>
      <button class="nav-item" onclick="switchNav('Account')">
        <span>👤</span>
        <span class="nav-label" data-ar="الحساب" data-en="Account">الحساب</span>
      </button>
      <button class="nav-item" onclick="switchNav('Terms')">
        <span>📄</span>
        <span class="nav-label" data-ar="الشروط" data-en="Terms">الشروط</span>
      </button>
    </nav>

    <!-- Floating Scroll Up & Down Controls -->
    <div style="position:fixed; bottom:75px; left:16px; display:flex; flex-direction:column; gap:8px; z-index:9999;">
      <button onclick="scrollToTop()" id="btnScrollTop" title="Scroll to Top / إلى الأعلى" style="background:#2563EB; color:#fff; border:none; width:40px; height:40px; border-radius:50%; box-shadow:0 4px 12px rgba(0,0,0,0.25); cursor:pointer; font-size:18px; display:flex; align-items:center; justify-content:center; transition:transform 0.15s;">
        ⬆️
      </button>
      <button onclick="scrollToBottom()" id="btnScrollBottom" title="Scroll to Bottom / إلى الأسفل" style="background:#475569; color:#fff; border:none; width:40px; height:40px; border-radius:50%; box-shadow:0 4px 12px rgba(0,0,0,0.25); cursor:pointer; font-size:18px; display:flex; align-items:center; justify-content:center; transition:transform 0.15s;">
        ⬇️
      </button>
    </div>
  </div>

  <script>
    let currentLang = 'ar';
    let isDark = false;
    let currentIssues = [];
    let activeIssue = null;

    const samples = [
      "ذهب محمد الى المدرسة، و هو يحمل كتبه وشاهد عصفورا جميلا.",
      "إن المعلمة يساعد الطلاب في فهم الدرس.",
      "كان الولد يلعب في الحديقةُ وشاهد عصفوراً جميلاً."
    ];

    function analyze(text) {
      const issues = [];
      
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
      if (/\s+([،,.!?؟؛])/.test(text)) {
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

    function runCheck() {
      const text = document.getElementById('textInput').value;
      const words = text.trim() ? text.trim().split(/\s+/).length : 0;
      document.getElementById('statWords').innerText = words + (currentLang === 'ar' ? ' كلمات' : ' words');
      document.getElementById('statChars').innerText = text.length + (currentLang === 'ar' ? ' أحرف' : ' chars');

      currentIssues = analyze(text);
      document.getElementById('statIssues').innerText = currentIssues.length + (currentLang === 'ar' ? ' ملاحظات' : ' issues');

      const container = document.getElementById('issuesList');
      container.innerHTML = '';

      if (currentIssues.length === 0) {
        container.innerHTML = '<span style="color:var(--success); font-size:12px; font-weight:bold;">' + (currentLang === 'ar' ? '✓ النص سليم وخالٍ من الأخطاء!' : '✓ Text is pristine and error-free!') + '</span>';
        document.getElementById('inspector').style.display = 'none';
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
    }

    function selectIssue(idx) {
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

    function applySingle() {
      if (!activeIssue) return;
      const textarea = document.getElementById('textInput');
      textarea.value = textarea.value.replace(activeIssue.orig, activeIssue.sugg);
      runCheck();
    }

    function applyAll() {
      let text = document.getElementById('textInput').value;
      const issues = analyze(text);
      for (const item of issues) {
        if (item.orig && item.sugg && !item.orig.includes('مسافة')) {
          text = text.split(item.orig).join(item.sugg);
        }
      }
      // Punctuation cleanups
      text = text.replace(/,/g, '،').replace(/\?/g, '؟').replace(/;/g, '؛');
      text = text.replace(/\s+([،,.!?؟؛])/g, '$1');
      text = text.replace(/(^|\s)و\s+([\u0600-\u06FF]+)/g, '$1و$2');

      document.getElementById('textInput').value = text;
      runCheck();
    }

    function fixAllErrors() {
      applyAll();
      const alertEl = document.getElementById('statusBanner');
      if (alertEl) {
        alertEl.style.display = 'block';
        alertEl.innerText = currentLang === 'ar' ? '✓ تم تصحيح كافة الأخطاء بنجاح!' : '✓ All errors fixed successfully!';
        setTimeout(() => { alertEl.style.display = 'none'; }, 4000);
      }
    }

    function loadSample(i) {
      document.getElementById('textInput').value = samples[i];
      runCheck();
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
    }

    function generateAiWriting() {
      const prompt = (document.getElementById('aiPromptInput').value || '').trim();
      const currentContent = (document.getElementById('textInput').value || '').trim();
      const tone = document.getElementById('aiToneSelect').value;
      const genBtn = document.getElementById('btnGenerateAi');

      genBtn.disabled = true;
      genBtn.innerText = 'جارٍ التوليد بالذكاء الاصطناعي...';

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
        genBtn.innerHTML = '<span>✨</span> اكتب الآن بالذكاء الاصطناعي';
      }, 700);
    }

    function copyAiResult() {
      if (!currentAiText) return;
      navigator.clipboard?.writeText(currentAiText);
      alert(currentLang === 'ar' ? 'تم نسخ النص المولد بنجاح' : 'Copied AI text successfully');
    }

    function applyAiResult() {
      if (!currentAiText) return;
      document.getElementById('textInput').value = currentAiText;
      runCheck();
    }

    function appendAiResult() {
      if (!currentAiText) return;
      const cur = document.getElementById('textInput').value.trim();
      document.getElementById('textInput').value = cur ? (cur + '\n\n' + currentAiText) : currentAiText;
      runCheck();
    }

    function clearText() {
      document.getElementById('textInput').value = '';
      runCheck();
    }

    function toggleTheme() {
      isDark = !isDark;
      document.body.classList.toggle('dark', isDark);
      document.getElementById('themeBtn').innerText = isDark ? '☀️' : '🌙';
    }

    function toggleLang() {
      currentLang = currentLang === 'ar' ? 'en' : 'ar';
      document.documentElement.dir = currentLang === 'ar' ? 'rtl' : 'ltr';
      document.documentElement.lang = currentLang;
      document.getElementById('langBtn').innerText = currentLang === 'ar' ? 'EN' : 'عربي';

      document.querySelectorAll('.nav-label').forEach(el => {
        el.innerText = el.getAttribute('data-' + currentLang);
      });
      runCheck();
    }

    function switchNav(name, el) {
      document.querySelectorAll('.view-container').forEach(v => v.classList.remove('active'));
      document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

      const targetView = document.getElementById('view' + name);
      if (targetView) targetView.classList.add('active');

      const activeEl = el || (typeof window !== 'undefined' && window.event ? window.event.currentTarget : null);
      if (activeEl) {
        activeEl.classList.add('active');
      } else {
        const found = document.querySelector('.nav-item[onclick*="' + name + '"]');
        if (found) found.classList.add('active');
      }
    }

    // Reader Mode & Accessibility Functions (وضع القارئ الصوتي)
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
        card.style.boxShadow = isReaderModeOpen ? '0 4px 14px rgba(37,99,235,0.25)' : 'none';
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
          box.style.color = '#FFFFFF';
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
    }

    function toggleReaderHighContrast() {
      isReaderHighContrast = !isReaderHighContrast;
      const btn = document.getElementById('btnReaderContrast');
      if (btn) btn.classList.toggle('active', isReaderHighContrast);
      updateReaderDisplay();
    }

    function speakCurrentText() {
      const text = document.getElementById('textInput').value.trim() || 'ذهب محمدٌ إلى المدرسةِ، وهو يحملُ كتبَه وشاهدَ عصفوراً جميلاً.';
      if (!text) return;

      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = 'ar-SA';
        utterance.rate = 0.9;
        
        utterance.onstart = () => {
          isCurrentlySpeaking = true;
          const lbl = document.getElementById('btnSpeakLabel');
          if (lbl) lbl.innerText = currentLang === 'ar' ? 'جارٍ النطق الصوتي الآن...' : 'Speaking aloud now...';
        };
        utterance.onend = utterance.onerror = () => {
          isCurrentlySpeaking = false;
          const lbl = document.getElementById('btnSpeakLabel');
          if (lbl) lbl.innerText = currentLang === 'ar' ? 'انطق النص الآن' : 'Read Aloud';
        };

        window.speechSynthesis.speak(utterance);
      } else {
        alert(currentLang === 'ar' ? 'ميزة النطق الصوتي غير مدعومة في متصفحك حالياً.' : 'Text-to-speech is not supported in this browser.');
      }
    }

    function stopSpeaking() {
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
      }
      isCurrentlySpeaking = false;
      const lbl = document.getElementById('btnSpeakLabel');
      if (lbl) lbl.innerText = currentLang === 'ar' ? 'انطق النص الآن' : 'Read Aloud';
    }

    // Voice Dictation (Speech Recognition in Arabic)
    let speechRecognizer = null;
    let isListeningToSpeech = false;

    function initSpeechRecognizer() {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      if (!SpeechRecognition) return null;
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

      recognizer.onerror = (e) => {
        console.error('Speech recognition error:', e);
        isListeningToSpeech = false;
        updateDictationUI(false);
      };

      recognizer.onend = () => {
        isListeningToSpeech = false;
        updateDictationUI(false);
        runCheck();
      };

      return recognizer;
    }

    function toggleSpeechRecognition() {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      if (!SpeechRecognition) {
        alert(currentLang === 'ar' ? 'خاصية التعرف الصوتي غير مدعومة في متصفحك. يرجى استخدام متصفح حديث مثل Chrome.' : 'Speech recognition is not supported in your browser. Please use Chrome.');
        return;
      }

      if (!speechRecognizer) {
        speechRecognizer = initSpeechRecognizer();
      }

      if (isListeningToSpeech) {
        speechRecognizer.stop();
        isListeningToSpeech = false;
        updateDictationUI(false);
      } else {
        try {
          speechRecognizer.start();
        } catch (e) {
          console.warn('Speech recognizer already started or reset:', e);
        }
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

    // Scroll Up / Down functionality
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

    // Initial check
    runCheck();
  </script>
</body>
</html>
`;

const server = http.createServer((req, res) => {
  if (req.url === '/health') {
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('OK');
    return;
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
