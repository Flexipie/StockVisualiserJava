# Stock Price Visualization - API Setup (Optional)

## Overview
The Stock Visualiser now includes **live stock price visualization** with historical price charts!

## How It Works

The app uses two data sources:

### 1. **Simulated Data (Default - Always Works)**
- Generates realistic 30-day price history for demo purposes
- No internet connection required
- No API key needed
- Perfect for testing and presentations

### 2. **Live Data from Alpha Vantage API (Optional)**
- Real stock market data
- Requires free API key
- 25 API calls per day (free tier)

---

## Getting a Free API Key (Optional)

If you want to use **real live data** instead of simulated data:

1. **Visit**: https://www.alphavantage.co/support/#api-key
2. **Enter your email** and get instant free API key
3. **Copy the API key** they send you

### Update the Code:

Open: `src/main/java/com/example/stockvisualiser/service/StockDataService.java`

Replace line 21:
```java
private static final String API_KEY = "demo";
```

With your key:
```java
private static final String API_KEY = "YOUR_API_KEY_HERE";
```

---

## Features

### Interactive Price Chart
- **30-day historical price data** displayed as a line chart
- **Automatic updates** when you select a stock
- **Refresh button** to reload data
- **Date formatting** (MM/DD) on X-axis
- **USD price** on Y-axis

### How to Use:
1. Go to the **"📈 Stocks"** tab
2. **Click on any stock** in the table
3. The chart automatically updates with 30 days of price history
4. Click **"Refresh Chart"** to reload the data

---

## Technical Details

### Supported Stocks:
- **AAPL** - Apple Inc.
- **GOOGL** - Alphabet Inc.
- **MSFT** - Microsoft
- **AMZN** - Amazon
- **TSLA** - Tesla
- **META** - Meta Platforms
- **NVDA** - NVIDIA
- **NFLX** - Netflix
- **AMD** - Advanced Micro Devices
- **INTC** - Intel

### Data Fallback:
The app is smart! It tries to fetch real data first, but if:
- No internet connection
- API limit reached
- Invalid API key
- API error

It automatically falls back to **realistic simulated data** so the app always works!

---

## For Demonstration/Testing

**No setup required!** The app works out of the box with simulated data that looks and behaves like real stock prices:
- ✅ Realistic price movements
- ✅ Daily volatility (±2%)
- ✅ Market trends (up/down/sideways)
- ✅ No weekends (skips Saturday/Sunday)
- ✅ Different price ranges per stock

---

## Why This Approach?

1. **Always Works**: App never fails due to API issues
2. **Demo-Friendly**: Works offline during presentations
3. **Extensible**: Easy to switch to real data later
4. **Free**: No paid API required for demo
5. **Educational**: Learn how real APIs work

---

## Future Enhancements

Want to add more features? Ideas:
- Multiple timeframes (7 days, 90 days, 1 year)
- Volume charts
- Technical indicators (MA, RSI, MACD)
- Compare multiple stocks
- Price alerts
- Export chart as image

---

**Enjoy your Stock Visualiser with live price charts!** 📈✨
