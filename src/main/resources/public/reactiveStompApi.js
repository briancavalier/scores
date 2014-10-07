var most = require('most');
var jiff = require('jiff');

// A simple stompjs adapter that returns Promises and Streams
// rather than using callbacks.

exports.connect = connect;
exports.getScoresStream = getScoresStream;

// Promisify stomp connect()
function connect(username, password, client) {
	return new Promise(function(resolve, reject) {
		client.connect(username, password, function() {
			resolve(client);
		}, reject);
	});
}

function getScoresStream(initDestination, updateDestination, client) {
	// Create a stream containing one full copy of the data, and
	// flatMap that to a stream containing the time-varying
	// current set of scores, by accumulating each patch
	// and emitting the updated scores data.
	return getInitialDataStream(initDestination, client)
		.flatMap(function(data) {
			return getUpdatesStream(updateDestination, client, data);
		});
}

function getInitialDataStream (initDestination, client) {
	// Await a copy of the data from the STOMP subscription
	// that is sending the full scores data, then unsubscribe.
	return streamFromStompJson(initDestination, client)
		.take(1);
}

function getUpdatesStream (updateDestination, client, data) {
	// Incrementally accumulate patches from the STOMP subscription
	// that is carrying JSON Patches onto the scores data to produce
	// an updated view of the scores.
	return streamFromStompJson(updateDestination, client)
		.startWith([])
		.scan(updateWithJsonPatch, data);
}

function streamFromStompJson(destination, stomp) {
	return most.create(function(add) {
		var sub = stomp.subscribe(destination, function(msg) {
			add(JSON.parse(msg.body));
		});

		// Return a dispose function to unsubscribe when all
		// consumers lose interest
		return sub.unsubscribe.bind(sub);
	});
}

function updateWithJsonPatch(data, patch) {
	return jiff.patch(patch, data);
}