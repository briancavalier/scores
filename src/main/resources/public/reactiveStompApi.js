var most = require('most');
var jiff = require('jiff');

// A simple stompjs adapter that returns Promises and Streams
// rather than using callbacks.

exports.connect = connect;
exports.signalFromChannels = signalFromChannels;

// Promisify stomp connect()
function connect(username, password, client) {
	return new Promise(function(resolve, reject) {
		client.connect(username, password, function() {
			resolve(client);
		}, reject);
	});
}

// Create a time-varying view of remote data using 2 channels
// One that provides the complete, initial data set, and another
// that provides incremental updates in JSON Patch format
function signalFromChannels(initDestination, updateDestination, client) {

	// Create a stream containing one full copy of the data, and
	// flatMap that to a stream containing the time-varying
	// current set of scores, using scan to accumulate each patch
	// and emit the updated data.
	return fromStompJson(initDestination, client)
		.take(1)
		.flatMap(function(data) {
			return fromStompJson(updateDestination, client)
				.startWith([])
				.scan(function(data, patch) {
					return jiff.patch(patch, data);
				}, data);
		});
}

function fromStompJson(destination, stomp) {
	return most.create(function(add) {
		var sub = stomp.subscribe(destination, function(msg) {
			add(JSON.parse(msg.body));
		});

		return sub.unsubscribe.bind(sub);
	});
}