var exec = require("cordova/exec");

exports.requestPermission = function (success, error) {
  exec(success, error, "ScheduleNotifications", "requestPermission", null);
};

exports.sendInstantNotification = function (args, success, error) {
  exec(success, error, "ScheduleNotifications", "sendInstantNotification", [
    args,
  ]);
};
