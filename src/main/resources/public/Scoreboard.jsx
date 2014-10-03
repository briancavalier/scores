var React = require('react');

module.exports = React.createClass({
	getInitialState: function() {
		return { scores: [] };
	},
	componentDidMount: function() {
		// Observe the scores signal, which always represents the
		// latest set of scores.
		var self = this;
		this.props.scores.observe(function(scores) {
			self.setState({ scores: scores });
		});
	},
	render: function() {
		var scores = this.state.scores.map(function(game) {
			return <li key={game.id} className="game">
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