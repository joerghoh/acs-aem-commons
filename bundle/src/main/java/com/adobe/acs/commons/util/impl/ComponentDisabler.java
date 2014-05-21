package com.adobe.acs.commons.util.impl;

import org.apache.felix.scr.annotations.Reference; 
import org.apache.felix.scr.annotations.Service; 
import org.apache.felix.scr.annotations.Property; 
import org.apache.felix.scr.annotations.Activate; 
import org.apache.felix.scr.ScrService; 
import org.apache.felix.scr.Component; 
import org.apache.sling.commons.osgi.PropertiesUtil; 
import org.osgi.service.component.ComponentContext; 
import org.osgi.service.event.Event; 
import org.osgi.service.event.EventHandler; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 


/**
 * Component disabler service
 * 
 * In Apache Felix the state of components and services is not persisted across restarts of its containing bundle.
 * For example, when you have a Bundle S containing a service S, and you manually stop the service S; after a
 * deactivate and activate of the bundle the service S is up again.
 * 
 * This service allows you to specify the names of components, which shouldn't be running. Whenever an OSGI service event is
 * fired, which services checks the status of this components and stops them if required.
 * 
 * Note 1: The component is always started, but this service takes care, that it is stopped immediately after. So if a behaviour
 * you don't like already happens during the activation of this service, you cannot prevent it using the mechanism here.
 * 
 * Note 2: Using this service should always be considered as a workaround. The primary focus should be to fix the component
 * you want to disable, so it's no longer required to disable it. If this component is part of Adobe AEM please raise a Daycare 
 * ticket for it.
 * 
 *
 */


@org.apache.felix.scr.annotations.Component(immediate=true,metatype=true,label="ACS AEM Common -- Component Disabler",description="Disables components by configuration") 
@Service() 
@Property(name="event.topics", value={"/org/osgi/framework/BundleEvent/STARTED","org/osgi/framework/ServiceEvent/REGISTERED"}, propertyPrivate=true) 
public class ComponentDisabler implements EventHandler { 


        @Reference 
        ScrService scrService; 

        private static final Logger log = LoggerFactory.getLogger(ComponentDisabler.class); 

        @Property(label="Disabled components", description="The names of the components/services you want to disable", cardinality=Integer.MAX_VALUE) 
        private static final String DISABLED_COMPONENTS = "services"; 
        private String[] disabledComponents; 

        @Activate 
        protected void Activate (ComponentContext ctx) { 
                disabledComponents = PropertiesUtil.toStringArray(ctx.getProperties().get(DISABLED_COMPONENTS), new String[]{}); 
                log.info("Disabling components and services" + disabledComponents.toString());
                handleEvent(null); 
        } 


        @Override 
        public void handleEvent(Event event) { 
                // We don't care about the event, we just need iterate all configured components and 
        		// try to disable them
        

                for (String component: disabledComponents) { 
                        disableComponent(component); 
                } 
        } 

        private boolean disableComponent (String componentName) { 
                Component[] comps = (Component[]) scrService.getComponents(componentName); 
                for (Component comp: comps){ 
                        if (comp.getState() != Component.STATE_DISABLED) { 
                                comp.disable(); 
                                log.info("Component {} disabled by configuration (pid={}) ", new Object[]{comp.getClassName(), comp.getConfigurationPid()}); 
                        } 
                } 
                return true; 
        } 
}