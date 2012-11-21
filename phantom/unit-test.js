if (phantom.args.length != 1) {
  console.log('Expected a target URL parameter.');
  phantom.exit(1);
}

var page = require('webpage').create();
var url = phantom.args[0];

page.onConsoleMessage = function (message) {
  console.log(message);
};

page.open(url, function (status) {

  if (status != "success") {
    console.log('Failed to open ' + url);
    phantom.exit(1);
  }

  var result = page.evaluate(function() {
    return inflections.test.run();
  });

  if (result != 0) {
    console.log("*** Tests failed! ***");
    phantom.exit(1);
  }

  console.log("Tests succeeded.");
  phantom.exit(0);
});
