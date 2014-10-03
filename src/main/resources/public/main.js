var most = require('most');
var React = require('react');
var Stomp = require('stompjs').Stomp;
var reactiveStompApi = require('./reactiveStompApi');

require('./main.css');

var Scoreboard = require('./Scoreboard.jsx');

var socketUrl = 'ws://localhost:8080/scores';

exports.main = function() {
	var node = document.querySelector('.scoreboard');

	// Connect: connection is a promise
	var connection = reactiveStompApi.connect('guest', 'guest', Stomp.client(socketUrl));

	// Create a time-varying signal view of the scores from the stomp connection
	var scores = most.fromPromise(connection).flatMap(function(client) {
		return reactiveStompApi.signalFromChannels('/app/scores', '/topic/scores', client);
	});

	// Create a Scoreboard, passing it the scores signal
	React.renderComponent(new Scoreboard({ scores: scores }), node);
};
