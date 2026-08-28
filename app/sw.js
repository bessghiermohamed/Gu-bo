const CACHE_NAME = 'talib-pwa-v1';
const OFFLINE_FALLBACK_URL = './talib_app.html';

// Static assets to precache on install
const PRECACHE_ASSETS = [
  './',
  './talib_app.html',
  './talib_admin.html',
  './manifest.json',
  './icons/favicon-16x16.png',
  './icons/favicon-32x32.png',
  './icons/icon-192x192.png',
  './icons/icon-maskable-192x192.png',
  './icons/icon-512x512.png',
  './icons/icon-maskable-512x512.png',
  './icons/apple-touch-icon.png'
];

// External assets (Fonts & Icons) to cache on demand
const EXTERNAL_HOSTS = [
  'fonts.googleapis.com',
  'fonts.gstatic.com',
  'cdnjs.cloudflare.com',
  'cdn.jsdelivr.net'
];

// Install Event - Precache Core Shell
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      console.log('[Tâlib Service Worker] Precaching app shell...');
      return cache.addAll(PRECACHE_ASSETS).catch((err) => {
        console.warn('[Tâlib Service Worker] Non-blocking precache item failed:', err);
      });
    }).then(() => {
      return self.skipWaiting();
    })
  );
});

// Activate Event - Clean Up Old Caches & Claim Clients
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cache) => {
          if (cache !== CACHE_NAME) {
            console.log('[Tâlib Service Worker] Removing old cache:', cache);
            return caches.delete(cache);
          }
        })
      );
    }).then(() => {
      return self.clients.claim();
    })
  );
});

// Fetch Event - Dynamic Caching Strategy
self.addEventListener('fetch', (event) => {
  const request = event.request;
  const url = new URL(request.url);

  // 1. NEVER intercept or cache auth endpoints or direct Supabase REST mutations
  // to ensure sensitive tokens and user passwords are never stored in service worker cache
  if (
    url.hostname.includes('supabase.co') &&
    (url.pathname.includes('/auth/v1') || request.method !== 'GET')
  ) {
    return; // Pass through directly to network
  }

  // 2. Cache-First for Fonts, Icons & CDN Libraries
  if (EXTERNAL_HOSTS.some(host => url.hostname.includes(host))) {
    event.respondWith(
      caches.match(request).then((cachedResponse) => {
        if (cachedResponse) {
          return cachedResponse;
        }
        return fetch(request).then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const responseToCache = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(request, responseToCache);
            });
          }
          return networkResponse;
        }).catch(() => {
          // If offline and not in cache, ignore
        });
      })
    );
    return;
  }

  // 3. Stale-While-Revalidate / Network-First for Navigation and HTML pages
  if (request.mode === 'navigate' || request.destination === 'document') {
    event.respondWith(
      fetch(request)
        .then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const responseToCache = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(request, responseToCache);
            });
          }
          return networkResponse;
        })
        .catch(async () => {
          console.log('[Tâlib Service Worker] Serving offline page fallback...');
          const cachedResponse = await caches.match(request);
          if (cachedResponse) return cachedResponse;
          const fallback = await caches.match(OFFLINE_FALLBACK_URL);
          if (fallback) return fallback;
          return new Response('<h1>أنت في وضع عدم الاتصال (Offline)</h1><p>يرجى التحقق من اتصالك بالإنترنت.</p>', {
            headers: { 'Content-Type': 'text/html; charset=utf-8' }
          });
        })
    );
    return;
  }

  // 4. Default Cache Falling Back to Network for static local assets
  event.respondWith(
    caches.match(request).then((cachedResponse) => {
      if (cachedResponse) {
        return cachedResponse;
      }
      return fetch(request).then((networkResponse) => {
        if (
          networkResponse &&
          networkResponse.status === 200 &&
          request.method === 'GET' &&
          !url.hostname.includes('supabase.co')
        ) {
          const responseToCache = networkResponse.clone();
          caches.open(CACHE_NAME).then((cache) => {
            cache.put(request, responseToCache);
          });
        }
        return networkResponse;
      }).catch(() => {
        // Return nothing if offline and not in cache
      });
    })
  );
});
