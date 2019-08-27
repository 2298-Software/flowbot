package com._2298software.apps.flowbot;

import java.util.List;

public class Flow {
    private String name;
    private String description;
    private List<Step> step;
    private String start;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Step> getSteps() {
        return step;
    }

    public void setSteps(List<Step> steps) {
        this.step = steps;
    }
    
    public boolean hasMoreSteps(){
        for(Step s : this.getSteps()){
            if(s.getStatus() == 0){
                return true;
            }
        }
        return false;
    }
    
    public Step getNextStep(Step currentStep) {
        Step nextStep = null;

        String nextStepName = currentStep.getOn_success();

        for(Step s : this.getSteps()) {
            if (nextStepName.equals(s.getName())) {
                nextStep = s;
            }
        }
        return nextStep;
    }

}