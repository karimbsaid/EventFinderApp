
# 📱 EventFinder

**EventFinder** is an Android application that helps users discover live events near them using the Ticketmaster API. Users can browse events by category, view detailed event information, add events to favorites, and manage them through integration with Supabase.

![eventfinder-banner](https://your-image-url-if-any.png)

---

### ✨ Features

- 🔍 Browse live events by category (e.g., Music, Arts & Theatre, Sports)
- 📍 Location-based search (defaults to New York City for now)
- ❤️ Mark events as favorites (stored in Supabase)
- 🔗 View ticket links and event details
- 📅 Add events to your calendar
- 🗺️ View event locations in Google Maps
- 🔐 JWT-based authentication with Supabase
- 🌐 Deep linking support for password reset

---

### 🛠️ Tech Stack

| Layer         | Technology                      |
|--------------|----------------------------------|
| Language      | Kotlin                          |
| UI Framework  | Android Jetpack (ViewModel, LiveData, Fragment) |
| Networking    | Retrofit + Coroutines           |
| Backend       | Supabase (PostgreSQL + Auth)    |
| External API  | Ticketmaster Discovery API      |
| Dependency Injection | ViewModel + Manual        |

---

### 📦 Installation

1. **Clone the repository:**

```bash
git clone https://github.com/your-username/eventfinder.git
cd eventfinder
```

2. **Open in Android Studio**

   File → Open → Select the `eventfinder` directory

3. **Set up your API keys:**

Create or update `local.properties` with your Ticketmaster API key:

```properties
TICKETMASTER_API_KEY=your_api_key_here
```

4. **Run the app**

Use an emulator or connect your Android device to build & run.

---

### 🔑 Supabase Configuration

Make sure to configure your Supabase project with:

- `favorites` table:
  - `event_id`: `text` (Primary Key)
  - `event_name`: `text`
  - `user_id`: `uuid`
- Enable Row-Level Security (RLS) and policies for authenticated access to `favorites`.

---

### 📂 Project Structure

```
📁 model         → Data models (Event, Category, Favorite, etc.)
📁 network       → Retrofit API clients (Ticketmaster, Supabase)
📁 ui
 ┣ 📁 adapter    → RecyclerView adapters
 ┣ 📁 home       → HomeFragment (categories + events)
 ┣ 📁 detail     → EventDetail activity
📁 viewmodel     → MainViewModel (shared state + logic)
📁 utils         → Helpers (TokenManager, formatting, etc.)
```

---

### 🚀 API Usage

**Ticketmaster Discovery API**  
- `GET /discovery/v2/events.json` to list events  
- Optional: pass `classificationName` for filtering categories  
- Omit `classificationName` when category is `"All"` to retrieve all results.

**Supabase API (via Retrofit)**  
- `GET /favorites`: retrieve favorite events  
- `POST /favorites`: add a favorite  
- `DELETE /favorites`: remove a favorite by `event_id`  

---

### 🔐 Authentication

- JWT tokens stored securely using `TokenManager`
- Passed in `Authorization: Bearer <token>` for Supabase API calls
- Used for filtering favorites per user

---

### 📅 Deep Linking Support

Supports password reset links via Supabase Auth — handles incoming links in app using Android intent filters.

---

### 🐞 Known Issues

- Current location is hardcoded to NYC. Future updates will include device-based geolocation.
- No offline support yet.
- Minimal UI customization – material theming to be improved.

---

### 📈 Future Enhancements

- 🌍 Dynamic location detection
- 🔔 Push notifications for upcoming events
- 🧠 Recommendation system using user favorites
- 🎨 Dark mode support

---

### 🙌 Contributing

Pull requests are welcome! For major changes, open an issue first to discuss what you'd like to change.

---

### 📄 License

[MIT License](LICENSE)
