# 📱 EventFinder

**EventFinder** is an Android application that helps users discover live events near them using the Ticketmaster API. Users can browse events by category, view detailed event information, add events to favorites, and manage them through integration with Supabase.


<img src="https://github.com/user-attachments/assets/a66afc25-106f-4482-950b-36ad3badaf5d" alt="homepage" width="300"/>
---

## 🎥 Video Demo

👉 [Watch Demo](https://your-video-demo-url.com)

---

## ✨ Features

- 🔍 Browse live events by category (e.g., Music, Arts & Theatre, Sports)
- 📍 Location-based search (defaults to New York City for now)
- ❤️ Mark events as favorites (stored in Supabase)
- 🔗 View ticket links and event details
- 📅 Add events to your calendar
- 🗺️ View event locations in Google Maps
- 🔐 JWT-based authentication with Supabase
- 🌐 Deep linking support for password reset

---

## 🖼️ Screenshots

### 🔐 Login
User logs in using credentials stored in Supabase Auth.  
<img src="https://github.com/user-attachments/assets/6bb8063e-624e-4ea2-a5eb-2121129cafb2" alt="loginpage" width="300"/>


---

### 📝 Signup
Create a new account using email and password.  
<img src="https://github.com/user-attachments/assets/2f3a3d0a-bdcd-41de-9e31-b54d5ad4b682" alt="signuppage" width="300"/>

---

### ❓ Forgot Password
Enter your email to receive a password reset link.  
<img src="https://github.com/user-attachments/assets/8fa18870-38cb-4647-b9d7-4c870c6cdc68" alt="forgetpasswordpage" width="300"/>

---

### 🔁 Reset Password
Set a new password by opening the deep link from the email.  
<img src="https://github.com/user-attachments/assets/6bb8063e-624e-4ea2-a5eb-2121129cafb2" alt="loginpage" width="300"/>

---

### 🏠 Home
Browse all events, filter by category, tap to view or favorite.  
<img src="https://github.com/user-attachments/assets/a66afc25-106f-4482-950b-36ad3badaf5d" alt="homepage" width="300"/>

---

### 📄 Event Detail
Details include share, map, calendar, favorite, and register options.  
<img src="https://github.com/user-attachments/assets/5f35a6aa-0b8c-49cf-819b-95aae1c49228" alt="loginpage" width="300"/>

---

### ❤️ My Favorites
Shows events you’ve added to favorites.  
<img src="https://github.com/user-attachments/assets/cb4c3801-4816-46c2-ba69-46cfa2bf2cec" alt="loginpage" width="300"/>

---

### 👤 Profile
Manage your profile and authentication state.  
<img src="https://github.com/user-attachments/assets/cbfaa27a-3857-4e8a-b9aa-c92187348bf9" alt="loginpage" width="300"/>

---

## 🛠️ Tech Stack

| Layer         | Technology                      |
|--------------|----------------------------------|
| Language      | Kotlin                          |
| UI Framework  | Android Jetpack (ViewModel, LiveData, Fragment) |
| Networking    | Retrofit + Coroutines           |
| Backend       | Supabase (PostgreSQL + Auth)    |
| External API  | Ticketmaster Discovery API      |
| Dependency Injection | ViewModel + Manual        |

---

## 📦 Installation

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
SUPABASE_API_KEY=your_api_key_here

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
 ┣ 📁 profile    → ProfileFragment
 ┣ 📁 myfavorite    → FavoriteFragment 
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
