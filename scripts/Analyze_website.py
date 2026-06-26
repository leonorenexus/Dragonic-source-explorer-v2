"""
Dragonic Source Explorer - Website Analysis Script
----------------------------------------------------
Runs inside the GitHub Actions "Website Analysis" workflow, triggered via
workflow_dispatch from the Android app.

Scope & safety:
  - Performs only standard, unauthenticated HTTP GET requests to the target
    URL and to its public /robots.txt and /sitemap.xml paths - the exact same
    requests any web browser makes when a person visits the page.
  - Does NOT attempt to bypass authentication, access controls, rate limits,
    or any private/internal resource.
  - Does NOT perform vulnerability scanning, port scanning, or any intrusive
    technique. It only reads what the server already serves publicly.
"""

import json
import os
import time
from urllib.parse import urlparse

import requests
from bs4 import BeautifulSoup

TARGET_URL = os.environ.get("TARGET_URL", "").strip()
OUTPUT_DIR = "report"
HEADERS = {
    "User-Agent": "DragonicSourceExplorer-Bot/1.0 (+public-resource-analyzer; respects robots.txt)"
}
TIMEOUT_SECONDS = 20


def fail(message: str):
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    with open(os.path.join(OUTPUT_DIR, "report.json"), "w") as f:
        json.dump({"url": TARGET_URL, "success": False, "error": message}, f, indent=2)
    raise SystemExit(message)


def check_public_path(base_scheme: str, base_netloc: str, path: str) -> bool:
    try:
        url = f"{base_scheme}://{base_netloc}{path}"
        r = requests.get(url, headers=HEADERS, timeout=10)
        return r.status_code == 200
    except requests.RequestException:
        return False


def detect_technologies(html: str, headers: dict, soup: BeautifulSoup) -> list:
    technologies = set()

    server_header = headers.get("Server", "")
    powered_by = headers.get("X-Powered-By", "")
    if server_header:
        technologies.add(server_header)
    if powered_by:
        technologies.add(powered_by)

    generator_tag = soup.find("meta", attrs={"name": "generator"})
    if generator_tag and generator_tag.get("content"):
        technologies.add(generator_tag["content"])

    lowered = html.lower()
    if "wp-content" in lowered:
        technologies.add("WordPress")
    if "_next/static" in lowered:
        technologies.add("Next.js")
    if "react" in lowered and (soup.find(attrs={"data-reactroot": True}) or "react-dom" in lowered):
        technologies.add("React (heuristic)")
    if "cdn.shopify.com" in lowered:
        technologies.add("Shopify")

    return sorted(technologies)


def main():
    if not TARGET_URL:
        fail("No website_url input provided")

    parsed = urlparse(TARGET_URL)
    if parsed.scheme not in ("http", "https") or not parsed.netloc:
        fail("Invalid URL: must be a public http(s) URL")

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    start = time.time()
    try:
        response = requests.get(TARGET_URL, headers=HEADERS, timeout=TIMEOUT_SECONDS, allow_redirects=True)
    except requests.RequestException as e:
        fail(f"Could not reach {TARGET_URL}: {e}")
        return

    elapsed_ms = round((time.time() - start) * 1000)
    html = response.text
    soup = BeautifulSoup(html, "html.parser")

    scripts = soup.find_all("script")
    stylesheets = soup.find_all("link", rel="stylesheet")
    images = soup.find_all("img")
    links = soup.find_all("a")
    meta_tags = soup.find_all("meta")
    total_resources = len(scripts) + len(stylesheets) + len(images)

    has_robots = check_public_path(parsed.scheme, parsed.netloc, "/robots.txt")
    has_sitemap = check_public_path(parsed.scheme, parsed.netloc, "/sitemap.xml")

    report = {
        "url": TARGET_URL,
        "success": True,
        "status_code": response.status_code,
        "response_time_ms": elapsed_ms,
        "page_size_bytes": len(response.content),
        "title": soup.title.string.strip() if soup.title and soup.title.string else None,
        "total_links": len(links),
        "total_scripts": len(scripts),
        "total_stylesheets": len(stylesheets),
        "total_images": len(images),
        "total_meta_tags": len(meta_tags),
        "total_resources": total_resources,
        "technologies_detected": detect_technologies(html, response.headers, soup),
        "has_robots_txt": has_robots,
        "has_sitemap_xml": has_sitemap,
        "response_headers": dict(response.headers),
    }

    with open(os.path.join(OUTPUT_DIR, "report.json"), "w") as f:
        json.dump(report, f, indent=2)

    technologies_label = ", ".join(report["technologies_detected"]) or "Not detected"
    html_report = f"""<!DOCTYPE html>
<html><head><meta charset="utf-8"><title>Dragonic Source Explorer Report</title></head>
<body style="background:#050810;color:#e6f1ff;font-family:monospace;padding:24px;">
<h1 style="color:#22d3ee;">Website Analysis Report</h1>
<p>URL: {TARGET_URL}</p>
<p>Status: {response.status_code} | Response time: {elapsed_ms} ms | Page size: {len(response.content)} bytes</p>
<p>Resources: {total_resources} (scripts: {len(scripts)}, stylesheets: {len(stylesheets)}, images: {len(images)})</p>
<p>Technologies: {technologies_label}</p>
<p>robots.txt: {'found' if has_robots else 'not found'} | sitemap.xml: {'found' if has_sitemap else 'not found'}</p>
</body></html>"""

    with open(os.path.join(OUTPUT_DIR, "report.html"), "w") as f:
        f.write(html_report)

    print(json.dumps(report, indent=2))


if __name__ == "__main__":
    main()
