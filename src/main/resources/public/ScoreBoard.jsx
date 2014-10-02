var React = require('react');
var jiff = require('jiff');

require('./main.css');

module.exports = React.createClass({
	getInitialState: function() {
		return { scores: [] };
	},
	componentDidMount: function() {
		// TODO: Would be nice if the patching could be done externally
		// and this component just received score arrays that react could
		var self = this;
		this.props.updates.observe(function(patch) {
			self.setState({ scores: jiff.patch(patch, self.state.scores) });
		});
	},
	render: function() {
		var scores = this.state.scores.map(function(game) {
			return <li className="game">
						<div className="home">
							<span className="name">{game.homeTeam}</span>
							<span className="score">{game.homeTeamScore}</span>
						</div>
						<div className="away">
							<span className="name">{game.awayTeam}</span>
							<span className="score">{game.awayTeamScore}</span>
						</div>
					</li>;
		});
		return <ul>{scores}</ul>;
	}
});