#!/usr/bin/env python3
"""
NahwiFix - Arabic Grammar & Punctuation Checker
Vercel Deployment Entrypoint & Standalone Web Server
Compatible with:
  - Vercel Serverless Python Runtime (@vercel/python: WSGI `app` & `handler`)
  - Standalone execution (`python3 main.py`)
  - Standard WSGI servers (Gunicorn, uWSGI, wsgiref)
"""

import os
import sys
import mimetypes
from urllib.parse import parse_qs, urlparse
from http.server import HTTPServer, BaseHTTPRequestHandler
from wsgiref.simple_server import make_server

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# MIME types configuration
mimetypes.add_type("text/html; charset=utf-8", ".html")
mimetypes.add_type("application/json", ".json")
mimetypes.add_type("application/pdf", ".pdf")
mimetypes.add_type("image/jpeg", ".jpg")
mimetypes.add_type("image/jpeg", ".jpeg")
mimetypes.add_type("image/png", ".png")
mimetypes.add_type("text/plain; charset=utf-8", ".txt")
mimetypes.add_type("application/xml", ".xml")

def get_file_response(file_path, content_type=None):
    """Safely read and return file bytes and content type."""
    if not os.path.isfile(file_path):
        return None, None
    if not content_type:
        content_type, _ = mimetypes.guess_type(file_path)
    if not content_type:
        content_type = "application/octet-stream"
    try:
        with open(file_path, "rb") as f:
            data = f.read()
        return data, content_type
    except Exception:
        return None, None

def route_request(path_str, query_str=""):
    """
    Central router that maps requested URL path to response content,
    status code, and headers.
    """
    clean_path = path_str.split("?")[0].rstrip("/")
    if not clean_path:
        clean_path = "/"

    # 1. Health check endpoint
    if clean_path in ("/health", "/api/health"):
        return 200, [("Content-Type", "text/plain; charset=utf-8")], b"OK"

    # 2. English Slides presentation
    if (clean_path in ("/slides_en", "/slides-en", "/slides_en.html", "/presentation/en")
            or (clean_path in ("/slides", "/presentation") and "lang=en" in query_str)):
        target = os.path.join(BASE_DIR, "slides_en.html")
        data, ctype = get_file_response(target, "text/html; charset=utf-8")
        if data:
            return 200, [("Content-Type", ctype)], data

    # 3. Arabic / Default Slides presentation
    if clean_path in ("/slides", "/slides.html", "/presentation"):
        target = os.path.join(BASE_DIR, "slides.html")
        data, ctype = get_file_response(target, "text/html; charset=utf-8")
        if data:
            return 200, [("Content-Type", ctype)], data

    # 4. Evaluation dataset PDF
    if clean_path in ("/evaluation_dataset.pdf", "/dataset.pdf", "/download/evaluation_dataset.pdf"):
        target = os.path.join(BASE_DIR, "evaluation_dataset.pdf")
        data, ctype = get_file_response(target, "application/pdf")
        if data:
            headers = [
                ("Content-Type", ctype),
                ("Content-Disposition", 'inline; filename="NahwiFix_Evaluation_Dataset.pdf"'),
                ("Content-Length", str(len(data))),
            ]
            return 200, headers, data

    # 5. Promo banner image
    if clean_path in ("/promo_banner.jpg", "/promo.jpg", "/ad.jpg"):
        target = os.path.join(BASE_DIR, "promo_banner.jpg")
        data, ctype = get_file_response(target, "image/jpeg")
        if data:
            headers = [
                ("Content-Type", ctype),
                ("Cache-Control", "public, max-age=86400"),
                ("Content-Length", str(len(data))),
            ]
            return 200, headers, data

    # 6. Static files by explicit filename at root
    static_files = {
        "/favicon-1500w.png": ("favicon-1500w.png", "image/png"),
        "/favicon.png": ("favicon-1500w.png", "image/png"),
        "/favicon.ico": ("favicon-1500w.png", "image/png"),
        "/robots.txt": ("robots.txt", "text/plain; charset=utf-8"),
        "/sitemap.xml": ("sitemap.xml", "application/xml; charset=utf-8"),
        "/llms.txt": ("llms.txt", "text/plain; charset=utf-8"),
        "/en.json": ("en.json", "application/json; charset=utf-8"),
    }
    if clean_path in static_files:
        filename, ctype = static_files[clean_path]
        target = os.path.join(BASE_DIR, filename)
        data, _ = get_file_response(target, ctype)
        if data:
            return 200, [("Content-Type", ctype), ("Content-Length", str(len(data)))], data

    # 7. Main Web Application (`index.html`)
    index_path = os.path.join(BASE_DIR, "index.html")
    if os.path.isfile(index_path):
        data, ctype = get_file_response(index_path, "text/html; charset=utf-8")
        if data:
            return 200, [("Content-Type", ctype), ("Content-Length", str(len(data)))], data

    # 8. Fallback to server.js extraction if index.html is missing
    server_js_path = os.path.join(BASE_DIR, "server.js")
    if os.path.isfile(server_js_path):
        try:
            with open(server_js_path, "r", encoding="utf-8") as f:
                content = f.read()
            start_tag = "<!DOCTYPE html>"
            end_tag = "</html>"
            s_idx = content.find(start_tag)
            e_idx = content.find(end_tag, s_idx)
            if s_idx != -1 and e_idx != -1:
                html_bytes = content[s_idx:e_idx + len(end_tag)].encode("utf-8")
                return 200, [("Content-Type", "text/html; charset=utf-8"), ("Content-Length", str(len(html_bytes)))], html_bytes
        except Exception:
            pass

    return 404, [("Content-Type", "text/plain; charset=utf-8")], b"404 Not Found"


# ==============================================================================
# 1. WSGI Entrypoint for Vercel (@vercel/python) and WSGI Servers (Gunicorn)
# ==============================================================================
def app(environ, start_response):
    """Standard WSGI entrypoint."""
    path = environ.get("PATH_INFO", "/")
    query = environ.get("QUERY_STRING", "")
    status_code, headers, body = route_request(path, query)

    status_messages = {
        200: "200 OK",
        404: "404 Not Found",
        500: "500 Internal Server Error"
    }
    status_str = status_messages.get(status_code, f"{status_code} Status")
    start_response(status_str, headers)
    return [body]


# ==============================================================================
# 2. Vercel BaseHTTPRequestHandler Entrypoint
# ==============================================================================
class handler(BaseHTTPRequestHandler):
    """Vercel BaseHTTPRequestHandler entrypoint."""
    def do_GET(self):
        parsed = urlparse(self.path)
        status_code, headers, body = route_request(parsed.path, parsed.query)
        self.send_response(status_code)
        for key, value in headers:
            self.send_header(key, value)
        self.end_headers()
        self.wfile.write(body)

    def do_HEAD(self):
        parsed = urlparse(self.path)
        status_code, headers, body = route_request(parsed.path, parsed.query)
        self.send_response(status_code)
        for key, value in headers:
            self.send_header(key, value)
        self.end_headers()

    def log_message(self, format, *args):
        # Quiet logger for production
        pass


# ==============================================================================
# 3. Standalone Execution (`python3 main.py`)
# ==============================================================================
if __name__ == "__main__":
    port = int(os.environ.get("PORT", os.environ.get("DEFAULT_APP_PORT", 3000)))
    print(f"🚀 NahwiFix server starting on http://0.0.0.0:{port}")
    try:
        with make_server("0.0.0.0", port, app) as httpd:
            print(f"✅ Serving on port {port} (WSGI)...")
            httpd.serve_forever()
    except Exception as e:
        print(f"WSGI server encountered exception: {e}. Falling back to HTTPServer...")
        server = HTTPServer(("0.0.0.0", port), handler)
        server.serve_forever()
