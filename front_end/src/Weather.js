import React, { Component } from 'react';
import { Card, CardHeader, CardBody, CardText } from 'reactstrap'

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
        fetch('http://'+window.location.hostname+':'+window.location.port+'/meteo')
            .then(res => res.json())
            .then(function(res) { console.log(res); return res; })
            .then(res => {
                if (res.temperature) {
                    this.setState({
                        temperature: res.temperature,
                        humidity: res.humidity,
                        description: res.description,
                        error: null,
                        lastUpdate: new Date()
                    })
                } else {
                    this.setState({
                        temperature: null,
                        humidity: null,
                        description: null,
                        error: res.error,
                        lastUpdate: new Date()
                    })
                }
            })
            .catch(err => {
                console.log(err);
                this.setState({
                    temperature: null,
                    humidity: null,
                    description: null,
                    error: "Unable to contact server",
                    lastUpdate: new Date()
                })}
            );
    }

    componentDidMount() {
        this.tick();
        this.intervalTick = setInterval(() => this.tick(), 10000);
    }

    componentWillUnmount() {
        clearInterval(this.intervalTick);
    }

    firstCaps(string)
    {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    render() {
        return (
            <div>
                <Card>
                    <CardHeader tag="h3">Météo</CardHeader>
                    <CardBody>
                        <CardText tag="div">
                            <div>
                                {this.state.temperature && <div>
                                    <div className= "d-inline">Température</div>
                                    <h4 className="d-inline p-1">{this.state.temperature.toFixed(1)}°C</h4></div>}
                                {this.state.humidity && <div>
                                    <div className="d-inline">Humidité</div>
                                    <h4 className="d-inline p-1">{this.state.humidity}%</h4></div>}
                            </div>
                            {this.state.description && <h4>{this.firstCaps(this.state.description)}</h4>}
                            {this.state.error && <p className="text-danger">Error: {this.state.error}</p>}
                            {this.state.lastUpdate && <p className="text-muted">Last Update: {this.state.lastUpdate.toLocaleDateString("fr-FR", {hour: 'numeric', minute: 'numeric', second: 'numeric'})}</p>}
                        </CardText>
                    </CardBody>
                </Card>
            </div>);
    }
}

export default Weather;

