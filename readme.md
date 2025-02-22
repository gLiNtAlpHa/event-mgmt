# 🚀 Event MGMT Web App 😎

## 🛠️ TL;DR

A dope, scalable web app running on **Tomcat** with layers that keep things
clean and modular. This ain't spaghetti code, it's structured AF.

## 🔥 Architecture Breakdown

Here's how we roll:

### 🎭 **Client Layer**

- **Web Client (WC)**: Browser-based UI.
- **Mobile Client (MC)**: Mobile app UI.

### 🎨 **Presentation Layer**

- **JSP Pages (JSP)**: HTML, but make it dynamic.
- **Servlet Controllers (Servlets)**: The traffic cops for requests.

### ⚙️ **Service Layer**

Where the magic happens:

- **Event Service (ES)**: Handles events.
- **User Service (US)**: Manages users.
- **RSVP Service (RS)**: Registers people.
- **ML Service (MS)**: AI-powered insights.
- **QR Code Service (QS)**: Generates/validates QR codes.
- **Email Service (ES)**: Shoots out emails like a pro.

### 📦 **Data Access Layer (DAO)**

- **Generic DAO (GD)**: One DAO to rule them all.
- **Entity Models:**
  - **EM**: Event Model
  - **UM**: User Model
  - **RM**: RSVP Model
  - **TM**: Training Model

### 💾 **Persistence Layer**

- **PostgreSQL (PG)**: Our DB of choice.

### 🔌 **External Services**

- **WEKA API (WEKA)**: For AI/ML tasks.
- **SMTP Server (SMTP)**: For email blasts.

## 🔁 How It All Connects

1. Clients interact via web or mobile.
2. JSP + Servlets handle UI logic.
3. Services do the heavy lifting.
4. DAO fetches/stores data.
5. ML Service calls **WEKA**.
6. Email Service uses **SMTP**.

## 🚀 Setup & Deployment

### **Prerequisites**

- Install **Apache Tomcat**.
- Set up **PostgreSQL** (create the DB, fam).
- Configure `server.xml` & `context.xml`.
- Deploy `.war` to Tomcat’s `webapps/`.

### **Deploy in 3 Steps**

```sh
mvn clean package  # Build the app
cp target/app.war $TOMCAT_HOME/webapps/  # Deploy the war
$TOMCAT_HOME/bin/startup.sh  # Start Tomcat
```

Boom! Open `http://localhost:8080/app` in your browser. 🎉

## 🎯 Future Upgrades

- REST API for 🔥 scalability.
- Add Redis caching for ⚡ speed.
- OAuth for 🔐 security.

## 📌 Final Thoughts

This stack is clean, scalable, and ready for action. No cap. Hit me up if you
wanna collab! 🚀
