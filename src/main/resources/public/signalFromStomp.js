var most = require('most');
var jiff = require('jiff');

module.exports = function streamFromStomp(initDestination, updateDestination, stomp) {

	return most.create(function(add) {

		stomp.connect('guest', 'guest', function() {
			// This is a bit messy due to adapting to the server
			// sending a full copy of the data on one channel, and
			// updates on another.

			// This up-front work will make it easy for consumers to
			// use the returned stream--it will look like a homogeneous
			// stream of scores.

			// Create a stream containing one full copy of the data, and
			// flatMap that to a stream containing the time-varying
			// current set of scores, using scan to accumulate each patch
			// and emit the updated data.
			var signal = fromStompJson(initDestination, stomp)
				.take(1)
				.flatMap(function(data) {
					return fromStompJson(updateDestination, stomp)
						.startWith([])
						.scan(function(data, patch) {
							return jiff.patch(patch, data);
						}, data)
				});

			add(signal);

		});

		// Return a dispose function for the outer stream;
		return stomp.disconnect.bind(stomp);

		// We've created a higher-order stream.
		// The outer stream contains one event: an inner stream of scores.
		// We use join() below to flatten the higher-order stream to
		// a first-order stream of time-varying score data.

	}).join();
};

function fromStompJson(destination, stomp) {
	return most.create(function(add) {
		var sub = stomp.subscribe(destination, function(msg) {
			add(JSON.parse(msg.body));
		});

		return sub.unsubscribe.bind(sub);
	});
}