const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');

const PORT = process.env.DEFAULT_APP_PORT || (process.env.PORT && process.env.PORT !== '8080' ? process.env.PORT : 3000);

// Path to main index.html
const indexPath = path.join(__dirname, 'index.html');

const server = http.createServer((req, res) => {
  if (req.url === '/health' || req.url === '/api/health') {
    res.writeHead(200, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('OK');
    return;
  }
  const reqUrl = req.url || '/';

  // Text-to-Speech audio streaming proxy for reliable Arabic reading
  if (reqUrl.startsWith('/api/tts') || reqUrl.startsWith('/tts')) {
    try {
      const parsed = new URL(reqUrl, 'http://localhost');
      const text = parsed.searchParams.get('text') || parsed.searchParams.get('q') || 'مرحبا';
      const lang = parsed.searchParams.get('lang') || 'ar';
      const cleanText = text.substring(0, 500);
      const googleTtsUrl = `https://translate.google.com/translate_tts?ie=UTF-8&tl=${encodeURIComponent(lang)}&client=tw-ob&q=${encodeURIComponent(cleanText)}`;
      
      https.get(googleTtsUrl, {
        headers: { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)' }
      }, (ttsRes) => {
        res.writeHead(ttsRes.statusCode || 200, {
          'Content-Type': 'audio/mpeg',
          'Cache-Control': 'public, max-age=86400',
          'Access-Control-Allow-Origin': '*'
        });
        ttsRes.pipe(res);
      }).on('error', (err) => {
        res.writeHead(500, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: err.message }));
      });
    } catch (e) {
      res.writeHead(500, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }
  if (reqUrl.startsWith('/slides_en') || reqUrl.startsWith('/slides-en') || reqUrl === '/presentation/en' || reqUrl === '/slides?lang=en') {
    const slidesPath = path.join(__dirname, 'slides_en.html');
    if (fs.existsSync(slidesPath)) {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
      fs.createReadStream(slidesPath).pipe(res);
      return;
    }
  }
  if (reqUrl === '/slides' || reqUrl.startsWith('/slides?') || reqUrl === '/presentation' || reqUrl === '/slides.html') {
    const isEnglish = reqUrl.includes('lang=en');
    const targetFile = isEnglish ? 'slides_en.html' : 'slides.html';
    const slidesPath = path.join(__dirname, targetFile);
    if (fs.existsSync(slidesPath)) {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
      fs.createReadStream(slidesPath).pipe(res);
      return;
    }
  }
  if (reqUrl === '/en.json') {
    const jsonPath = path.join(__dirname, 'en.json');
    if (fs.existsSync(jsonPath)) {
      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      fs.createReadStream(jsonPath).pipe(res);
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
  const staticIcons = {
    '/favicon.ico': { file: 'favicon.ico', type: 'image/x-icon' },
    '/favicon.png': { file: 'favicon.png', type: 'image/png' },
    '/favicon.svg': { file: 'favicon.svg', type: 'image/svg+xml' },
    '/favicon-192.png': { file: 'favicon-192.png', type: 'image/png' },
    '/favicon-512.png': { file: 'favicon-512.png', type: 'image/png' },
    '/favicon-1500w.png': { file: 'favicon-1500w.png', type: 'image/png' },
    '/logo.png': { file: 'logo.png', type: 'image/png' },
    '/logo.svg': { file: 'logo.svg', type: 'image/svg+xml' }
  };
  if (staticIcons[req.url]) {
    const assetPath = path.join(__dirname, staticIcons[req.url].file);
    if (fs.existsSync(assetPath)) {
      const stat = fs.statSync(assetPath);
      res.writeHead(200, {
        'Content-Type': staticIcons[req.url].type,
        'Content-Length': stat.size,
        'Cache-Control': 'public, max-age=86400'
      });
      fs.createReadStream(assetPath).pipe(res);
      return;
    }
  }
  if (req.url === '/evaluation_dataset.pdf' || req.url === '/dataset.pdf' || req.url === '/download/evaluation_dataset.pdf' || req.url === '/evaluation_dataset_en.pdf') {
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

  // Serve index.html dynamically
  if (fs.existsSync(indexPath)) {
    res.writeHead(200, {
      'Content-Type': 'text/html; charset=utf-8',
      'Cache-Control': 'no-cache, no-store, must-revalidate',
      'Pragma': 'no-cache',
      'Expires': '0'
    });
    fs.createReadStream(indexPath).pipe(res);
  } else {
    res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('404 Not Found');
  }
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`NahwiFix application server listening on port ${PORT}`);
});
