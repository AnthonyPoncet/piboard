import React, { Component } from 'react';
import './App.css';

import Clock from './Clock';
import GoogleApis from './GoogleApis';
import Weather from './Weather';

class App extends Component {
    render() {
        return (
        <div class="container">
            <div class="row">
                <div class="col-sm"><Clock/></div>
                <div class="col-sm"><Weather/></div>
            </div>
            <div class="row">
                <div class="col-sm"><GoogleApis/></div>
            </div>
        </div>);
    }
}

export default App;
