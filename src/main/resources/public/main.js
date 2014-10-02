var most = require('most');
var React = require('react');
var Stomp = require('stompjs').Stomp;

var Scoreboard = require('./Scoreboard.jsx');
var streamFromStomp = require('./streamFromStomp');

exports.main = function() {
	var node = document.querySelector('.scoreboard');
	var stomp = Stomp.over(new WebSocket('ws://localhost:8080/scores'));

	// Get a stream of JSON Patch updates over stomp
	var updates = streamFromStomp('/app/scores', 'topic/scores', stomp);

	// Create a Scoreboard, passing it a stream of updates
	React.renderComponent(new Scoreboard({ updates: updates }), node);
};

