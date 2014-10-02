var most = require('most');
var jiff = require('jiff');

module.exports = function streamFromStomp(initDestination, updateDestination, stomp) {

	return most.create(function(add) {

		stomp.connect('guest', 'guest', function() {
			// This is a bit messy due to adapting to the server
			// sending a full copy of the data on one channel, and
			// updates on another.

			// This up-front work will make it easy for consumers to
			// use the returned stream--it will look like a homogenous
			// stream of JSON Patches.

			// A most-stomp add-on could help here.

			// Create a stream containing the one and only initial copy
			// of the full data, transformed into a replace-whole-document
			// so that consumers of the outer stream (returned above)
			// get a homogeneous stream of patches.
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

			// We're creating a higher-order stream.
			// The outer stream contains one event: an inner stream of patches
			// We use join() below to flatten the higher-order stream to
			// a first-order stream of patches.
		});

		// Return a dispose function for the outer stream;
		return stomp.disconnect.bind(stomp);

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