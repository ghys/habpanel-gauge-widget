# A gauge widget for HABPanel

This bundle registers a gauge widget for [HABPanel](https://github.com/openhab/org.openhab.ui.habpanel) and its associated resources.

It can be built with Maven (`mvn clean package`), and installed like another bundle (for instance, drop in openHAB's `addons` folder).

It also serves as a demonstration on how additional code, here an AngularJS directive, registered as a local static resource when the bundle is activated, can be injected dynamically into a HABPanel widget template.

### Acknowledgments

This project contains a gauge directive based on [this Gist](https://gist.github.com/tomerd/1499279) by Tomer Doron - huge thanks to him!
