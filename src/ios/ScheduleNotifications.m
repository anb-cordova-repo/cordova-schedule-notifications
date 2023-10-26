/********* ScheduleNotifications.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <UserNotifications/UserNotifications.h>
#import "ScheduleNotifications.h"

@interface ScheduleNotifications ()

@property(strong, nonatomic) UNUserNotificationCenter *center;
@property(NS_NONATOMIC_IOSONLY, nullable, weak)
    id<UNUserNotificationCenterDelegate>
        delegate;
@property(readonly, nonatomic, retain) NSMutableArray *eventQueue;

@end

@implementation ScheduleNotifications

- (void)requestPermission:(CDVInvokedUrlCommand *)command {
  NSSetUncaughtExceptionHandler(&uncaughtExceptionHandler);
  UNAuthorizationOptions options =
      (UNAuthorizationOptionBadge | UNAuthorizationOptionSound |
       UNAuthorizationOptionAlert);
  UNUserNotificationCenter *center =
      [UNUserNotificationCenter currentNotificationCenter];
  [center requestAuthorizationWithOptions:options
                        completionHandler:^(BOOL granted,
                                            NSError *_Nullable error) {
                          if (!granted) {
                            NSLog(@"User has declined notifications");
                          }
                        }];
}

- (void)sendInstantNotification:(CDVInvokedUrlCommand *)command {
  [self.commandDelegate runInBackground:^{
    NSSetUncaughtExceptionHandler(&uncaughtExceptionHandler);
    NSDictionary *jsonData = command.arguments[0];
    NSString *title = jsonData[@"title"];
    NSString *message = jsonData[@"message"];
    UNMutableNotificationContent *content =
        [[UNMutableNotificationContent alloc] init];
    content.title = title;
    content.body = message;
    UNTimeIntervalNotificationTrigger *trigger =
        [UNTimeIntervalNotificationTrigger triggerWithTimeInterval:1
                                                           repeats:NO];
    NSString *identifier = @"UYLLocalNotification";
    UNNotificationRequest *request =
        [UNNotificationRequest requestWithIdentifier:identifier
                                             content:content
                                             trigger:trigger];

    UNUserNotificationCenter *center =
        [UNUserNotificationCenter currentNotificationCenter];
    [center requestAuthorizationWithOptions:(UNAuthorizationOptionAlert |
                                             UNAuthorizationOptionSound |
                                             UNAuthorizationOptionBadge)
                          completionHandler:^(BOOL granted,
                                              NSError *_Nullable error) {
                            if (!granted) {
                              NSLog(@"User has declined notifications");
                            }
                          }];

    [center addNotificationRequest:request
             withCompletionHandler:^(NSError *_Nullable error) {
               if (error != nil) {
                 NSLog(@"Error scheduling notification: %@",
                       error.localizedDescription);
               }
             }];
  }];
}
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:
             (void (^)(UNNotificationPresentationOptions options))
                 completionHandler {
  completionHandler(UNNotificationPresentationOptionList |
                    UNNotificationPresentationOptionAlert |
                    UNNotificationPresentationOptionSound |
                    UNNotificationPresentationOptionBadge);
}

void uncaughtExceptionHandler(NSException *exception) {
  NSLog(@"CRASH: %@", exception);
  NSLog(@"Stack Trace: %@", [exception callStackSymbols]);
  // Internal error reporting
}
#pragma mark -
#pragma mark Life Cycle

/**
 * Registers obervers after plugin was initialized.
 */
- (void)pluginInitialize {
  _eventQueue = [[NSMutableArray alloc] init];
  _center = [UNUserNotificationCenter currentNotificationCenter];
  _delegate = _center.delegate;

  _center.delegate = self;

  //call requestPermission to register for remote notifications
  [self requestPermission:nil];
}

@end
