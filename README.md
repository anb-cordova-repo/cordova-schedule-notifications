# Cordova Schedule Notification Plugin

A Cordova plugin that allows users to send local instant notifications.

## Installation

```bash
cordova plugin add https://github.com/ZarishIqbal/cordova-schedule-notifications.git
```

Make sure to replace the logo.png at `src/android` with the logo of your application.

## Usage

The plugin provides two methods to interact with notifications:

### 1. Request for notification permission

```javascript
cordova.plugins.ScheduleNotifications.requestPermission(successCallback, errorCallback);
```

### 2. Send Instant Notification

```javascript
cordova.plugins.ScheduleNotifications.sendInstantNotification({ title: "Test Notification", message: "This is a test notification" }, successCallback, errorCallback);
```

