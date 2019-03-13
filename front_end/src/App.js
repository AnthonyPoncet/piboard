import React, { Component } from 'react';
import './App.css';

import Clock from './Clock';
import GoogleApis from './GoogleApis';
import Weather from './Weather';

import { Container, Col, Row } from 'reactstrap';


class App extends Component {
    render() {
        return (
        <Container>
            <Row>
                <Col xs="6" sm="6" md="6" ls="6" xl="6">
                    <Row><Clock/></Row>
                    <Row><GoogleApis/></Row>
                </Col>
                <Col xs="6" sm="6" md="6" ls="6" xl="6">
                    <Row><Weather/></Row>
                </Col>
            </Row>
        </Container>);
    }
}

export default App;
