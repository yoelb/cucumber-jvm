package cucumber.internal;

import cucumber.Given;
import cucumber.Then;
import cucumber.When;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StepMother {
    private final List<Class> stepsClasses = new ArrayList<Class>();
    private List<StepDefinition> stepDefinitions = new ArrayList<StepDefinition>();

    public void add(Class stepsClass) {
        stepsClasses.add(stepsClass);
    }

    public void newWorld() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        for(Class stepsClass : stepsClasses) {
            pico.addComponent(stepsClass);
        }

        for(Object stepObject : pico.getComponents()) {
            for (Method method : stepObject.getClass().getMethods()) {
                String regexpString = null;
                if (method.isAnnotationPresent(Given.class)) {
                    regexpString = method.getAnnotation(Given.class).value();
                } else if (method.isAnnotationPresent(When.class)) {
                    regexpString = method.getAnnotation(When.class).value();
                } else if (method.isAnnotationPresent(Then.class)) {
                    regexpString = method.getAnnotation(Then.class).value();
                }
                if(regexpString != null) {
                    stepDefinitions.add(new StepDefinition(regexpString, stepObject, method));
                }
            }
        }
    }

    public List<StepDefinition> getStepDefinitions() {
        return stepDefinitions;
    }
}