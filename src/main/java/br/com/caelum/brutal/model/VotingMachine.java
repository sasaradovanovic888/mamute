package br.com.caelum.brutal.model;

import br.com.caelum.brutal.dao.VoteDAO;
import br.com.caelum.brutal.model.interfaces.Votable;
import br.com.caelum.brutal.reputation.rules.KarmaCalculator;
import br.com.caelum.brutal.reputation.rules.ReceivedVoteEvent;
import br.com.caelum.brutal.reputation.rules.VotedAtSomethingEvent;
import br.com.caelum.vraptor.ioc.Component;

@Component
public class VotingMachine {
    private VoteDAO votes;
    private final KarmaCalculator karmaCalculator;

    public VotingMachine(VoteDAO votes, KarmaCalculator karmaCalculator) {
        this.votes = votes;
        this.karmaCalculator = karmaCalculator;
    }

    public void register(Votable votable, Vote current, Class<?> votableType) {
        User voter = current.getAuthor();
        User votableAuthor = votable.getAuthor();
        if (votable.getAuthor().getId().equals(voter.getId())) {
            throw new IllegalArgumentException("an author can't vote its own votable");
        }
        
        Vote previous = votes.previousVoteFor(votable.getId(), voter, votableType);

        if (previous != null) {
            ReputationEvent receivedVote = new ReceivedVoteEvent(previous.getType(), votable).reputationEvent();
			votableAuthor.descreaseKarma(karmaCalculator.karmaFor(receivedVote));
			ReputationEvent votedAtSomething = new VotedAtSomethingEvent(previous, votable).reputationEvent();
            voter.descreaseKarma(karmaCalculator.karmaFor(votedAtSomething));
        }
        votable.substitute(previous, current);
        
        ReputationEvent receivedVote = new ReceivedVoteEvent(current.getType(), votable).reputationEvent();
        votableAuthor.increaseKarma(karmaCalculator.karmaFor(receivedVote));
        ReputationEvent votedAtSomething = new VotedAtSomethingEvent(previous, votable).reputationEvent();
        voter.increaseKarma(karmaCalculator.karmaFor(votedAtSomething));
    }

}
