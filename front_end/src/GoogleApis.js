import React, { Component } from 'react';
import { Table, Card, CardHeader, CardBody, CardText } from 'reactstrap';

function convertDate(jsonDate) {
    var d = new Date(0);
    d.setUTCSeconds(jsonDate.epochSec); //No shift needed
    return d;
}

class GoogleApis extends Component {
    constructor(props) {
        super(props);
        this.state = {
            events : []
        };
    }

    adaptEvent(item, index) {
        return { start : convertDate(item.start) , end: convertDate(item.end), organizer: item.organizer, summary: item.summary }
    }

    tick() {
        fetch('http://localhost:8080/calendar')
            .then(res => res.json())
            .then(function(res) { console.log(res); return res; })
            .then(data => {
                this.setState({ events: data.map(this.adaptEvent) });
            });
    }

    componentDidMount() {
        this.tick();
        this.intervalTick = setInterval(() => this.tick(), 10000);
    }

    renderEvent(event) {
        return (
        <Card>
            <CardHeader>{event.start.toDateString()}</CardHeader>
            <CardBody width="100%">
                <CardText>
                    <p>{event.summary}</p>
                    <p className="text-muted">{event.start.getHours()}:{event.start.getMinutes()} * {event.end.getHours()}:{event.end.getMinutes()}</p>
                </CardText>
            </CardBody>
        </Card>);
    }

    renderEvent2(event) {
        return (
        <Table>
            <thead class="bg-primary">
                <tr>
                <th>{event.start.toDateString()}</th>
                </tr>
            </thead>
            <tbody>
                <tr><th>
                    <p>{event.summary}</p>
                    <p className="text-muted">{event.start.getHours()}:{event.start.getMinutes()} ● {event.end.getHours()}:{event.end.getMinutes()}</p>
                </th></tr>
            </tbody>
        </Table>);
    }

    renderEvents(events) {
        if (events.length === 0) {
            return;
        }

        let navItems = [];
        var i;
        for (i = 0; i < events.length; i++) {
            navItems.push(this.renderEvent2(events[i]));
        }

        return navItems;
    }

    render() {
        return(<div>{this.renderEvents(this.state.events)}</div>);
    }

}

export default GoogleApis

