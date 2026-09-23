import sys
import os

# Ensure parent directory is in sys.path so main.py is importable
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from main import app, handler

# Export for Vercel Serverless Functions
__all__ = ["app", "handler"]
