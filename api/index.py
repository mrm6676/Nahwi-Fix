import sys
import os

# Ensure project root directory is in sys.path so main.py is importable
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if BASE_DIR not in sys.path:
    sys.path.insert(0, BASE_DIR)

from main import app, handler

# Export for Vercel Serverless Functions
__all__ = ["app", "handler"]
