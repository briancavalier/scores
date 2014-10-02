var most = require('most');
var React = require('react');
var Stomp = require('stompjs').Stomp;

require('./main.css');

var Scoreboard = require('./Scoreboard.jsx');
var signalFromStomp = require('./signalFromStomp');

exports.main = function() {
	var node = document.querySelector('.scoreboard');
	var stomp = Stomp.over(new WebSocket('ws://localhost:8080/scores'));

	// Get a signal representing the "latest set of scores" over stomp
	var scores = signalFromStomp('/app/scores', '/topic/scores', stomp);

	// Create a Scoreboard, passing it the scores signal
	React.renderComponent(new Scoreboard({ scores: scores }), node);
};

