package lexical.model

class StateTable(
     var states: List<State> = arrayListOf(),
     val stateTransitions: Transition = Transition()
)