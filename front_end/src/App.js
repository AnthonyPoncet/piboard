import React, { Component } from 'react';

import Clock from './Clock';
import GoogleApis from './GoogleApis';
import Weather from './Weather';

import RGL, { WidthProvider } from "react-grid-layout";
const ReactGridLayout = WidthProvider(RGL);

class App extends Component {
    static defaultProps = {
        className: "layout",
        items: 3,
        cols: 12,
        rowHeight: 30,
        onLayoutChange: function() {},
        // This turns off compaction so you can place items wherever.
        compactType: null,
        // This turns off rearrangement so items will not be pushed arround.
        preventCollision: true
    };

    constructor(props) {
        super(props);

        const layout = [
         {i: 'clock', x: 4, y: 0, w: 1.4, h: 3, isResizable: false},
         {i: 'weather', x: 8, y: 0, w: 2, h: 5.5, isResizable: false },
         {i: 'calendar', x: 2, y: 3, w: 6, h: 3, maxW: 6, minH: 1.4, maxH: 3 } //1.4 being no events, 3 one event
        ];
        this.state = { layout };
    }

    onLayoutChange(layout) {
        this.props.onLayoutChange(layout);
    }

    render() {
        return (
        <div>
            <ReactGridLayout
                    layout={this.state.layout}
                    onLayoutChange={this.onLayoutChange}
                    {...this.props}
                  >
                <div key="clock"><Clock/></div>
                <div key="weather"><Weather/></div>
                <div key="calendar"><GoogleApis/></div>
            </ReactGridLayout>
        </div>
        )
    }
}

export default App;
