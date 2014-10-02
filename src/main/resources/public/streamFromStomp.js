var most = require('most');

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
			var initial = most.create(function(add, end) {
				var subscription = stomp.subscribe(initDestination, function(msg) {
					add([{ op: "replace", path: "", value: JSON.parse(msg.body) }]);
					end();
				});

				// Return a dispose function to call when this stream ends;
				return subscription.unsubscribe.bind(subscription);
			});

			// Create a stream containing all the update patches.  This one
			// is easy since the channel already contains JSON Patch.
			var updates = most.create(function(add) {
				var subscription = stomp.subscribe(updateDestination, function(msg) {
					add(JSON.parse(msg.body));
				});

				// Return a dispose function to call when this stream ends;
				return subscription.unsubscribe.bind(subscription);
			});

			// We're creating a higher-order stream here.
			// The outer stream contains one event: an inner stream of patches
			// We use join() below to flatten the higher-order stream to
			// a first-order stream of patches.
			add(initial.concat(updates));
		});

		// Return a dispose function for the outer stream;
		return stomp.disconnect.bind(stomp);

	}).join();
};