import React, { Component } from 'react';
import { Card, CardBody, CardText } from 'reactstrap'

class Clock extends Component {
    constructor(props) {
        super(props);
        this.state = { time: new Date() };
    }

    componentDidMount() {
        this.intervalTick = setInterval(() => this.tick(), 1000);
    }

    componentWillUnmount() {
        clearInterval(this.intervalTick);
    }

    tick() {
        this.setState({ time: new Date() });
    }

    formatNum(num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return num;
        }
    }

    render() {
        /*return (
            <div>
                <Card>
                    <CardBody>
                        <CardText tag="div">
                            <h1 className="d-inline">{this.formatNum(this.state.time.getHours())}:{this.formatNum(this.state.time.getMinutes())}</h1>
                            <h4 className="d-inline">{this.formatNum(this.state.time.getSeconds())}</h4>

                            <p>{this.state.time.toLocaleDateString("fr-FR", {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'})}</p>
                        </CardText>
                    </CardBody>
                </Card>
            </div>
        );*/
        return(<div className="bg-primary">
            <h1 className="d-inline">{this.formatNum(this.state.time.getHours())}:{this.formatNum(this.state.time.getMinutes())}</h1>
            <h4 className="d-inline">{this.formatNum(this.state.time.getSeconds())}</h4>
            <p>{this.state.time.toLocaleDateString("fr-FR", {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'})}</p>
            </div>);
    }
}

export default Clock;

