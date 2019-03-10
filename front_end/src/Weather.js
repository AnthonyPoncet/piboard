import React, { Component } from 'react';
import { Card, CardHeader, CardBody, CardText } from 'reactstrap'

let Api_Key = 'e06ab83a1a301ea1e977e34f8e856940';

class Weather extends Component {
    constructor(props) {
        super(props);
        this.state = {
            temperature: null,
            humidity: null,
            description: null,
            error: "Loading",
            lastUpdate: null
        };
    }

    tick() {
        fetch('http://api.openweathermap.org/data/2.5/weather?units=metric&lang=fr&q=paris,fr&appid=' + Api_Key)
            .then(res => res.json())
            .then(function(res) { console.log(res); return res; })
            .then(res => this.setState({
                temperature: res.main.temp,
                humidity: res.main.humidity,
                description: res.weather[0].description,
                error: null,
                lastUpdate: new Date()
                }));
    }

    componentDidMount() {
        this.tick();
        this.intervalTick = setInterval(() => this.tick(), 100000000000);
    }

    componentWillUnmount() {
        clearInterval(this.intervalTick);
    }

    render() {
        return (
            <div>
                <Card>
                    <CardHeader tag="h3">Météo</CardHeader>
                    <CardBody>
                        <CardText>
                            <p>
                                {this.state.temperature && <div>
                                    <div className= "d-inline">Température</div>
                                    <h4 className="d-inline p-1">{this.state.temperature.toFixed(1)}°C</h4></div>}
                                {this.state.humidity && <div>
                                    <div className="d-inline">Humidité</div>
                                    <h4 className="d-inline p-1">{this.state.humidity}%</h4></div>}
                            </p>
                            {this.state.description && <h4>{this.state.description}</h4>}
                            {this.state.error && <p className="text-danger">Error: {this.state.error}</p>}
                            {this.state.lastUpdate && <p className="text-muted">Last Update: {this.state.lastUpdate.toDateString()}</p>}
                        </CardText>
                    </CardBody>
                </Card>
            </div>);
    }
}

export default Weather;

