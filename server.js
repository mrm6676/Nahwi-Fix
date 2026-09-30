const http = require('http');
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

  // Serve index.html dynamically
  if (fs.existsSync(indexPath)) {
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    fs.createReadStream(indexPath).pipe(res);
  } else {
    res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('404 Not Found');
  }
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`NahwiFix application server listening on port ${PORT}`);
});
