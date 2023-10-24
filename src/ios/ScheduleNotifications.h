#import <Cordova/CDVPlugin.h>

@import UserNotifications;

@interface ScheduleNotifications : CDVPlugin <UNUserNotificationCenterDelegate>

- (void)requestPermission:(CDVInvokedUrlCommand *)command;
- (void)sendInstantNotification:(CDVInvokedUrlCommand *)command;

@end