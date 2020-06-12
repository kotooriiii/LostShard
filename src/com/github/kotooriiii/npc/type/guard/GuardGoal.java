//package com.github.kotooriiii.npc.official.guard;
//
//import net.citizensnpcs.api.ai.Goal;
//import net.citizensnpcs.api.ai.GoalSelector;
//
//public class GuardGoal implements Goal {
//    private Object state;
//    private GoalSelector selector; // the current selector
//    public void reset() {
//        state = null;
//        // this method can be called at any time - tear down any state
//    }
//
//    @Override
//    public void run(GoalSelector goalSelector) {
//
//    }
//
//    public void run() {
//        if(!npcIsCool()) {
//            selector.finish(); // stops execution
//        } else if (npcIsAwesome()){
//            selector.select(new AwesomeGoal()); // this switches execution to AwesomeGoal and stops execution of this goal.
//        } else if (npcNeedsCool()) {
//            selector.selectAdditional(new AccumulateCoolGoal()); // AccumulateCoolGoal executes concurrently to this goal.
//        }
//    }
//    public boolean shouldExecute(GoalSelector selector) {
//        if (npcIsCool()) {
//            this.selector = selector;
//            return true;
//        }
//        return false;
//    }
//}
