import React, { Component } from 'react';

import Clock from './Clock';
import GoogleApis from './GoogleApis';
import Weather from './Weather';

import { Container, Col, Row } from 'reactstrap';

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
        verticalCompact: false,
        // This turns off rearrangement so items will not be pushed arround.
        preventCollision: true
    };

    constructor(props) {
        super(props);

        const layout = [
         {i: 'clock', x: 0, y: 0, w: 2, h: 2},
         {i: 'calendar', x: 0, y: 4, w: 6, h: 2},
         {i: 'weather', x: 6, y: 0, w: 2, h: 2}
        ];
        this.state = { layout };
    }

    onLayoutChange(layout) {
        this.props.onLayoutChange(layout);
    }

    render() {
        return (
            <ReactGridLayout
                    layout={this.state.layout}
                    onLayoutChange={this.onLayoutChange}
                    {...this.props}
                  >
                <div key="clock"><Clock/></div>
                <div key="calendar"><GoogleApis/></div>
                <div key="weather"><Weather/></div>
            </ReactGridLayout>
        )
    }
}

export default App;
