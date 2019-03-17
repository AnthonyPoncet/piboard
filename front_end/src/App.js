import React, { Component } from 'react';

import Clock from './Clock';
import GoogleApis from './GoogleApis';
import Weather from './Weather';

import { Container, Col, Row } from 'reactstrap';


class App extends Component {
    render() {
        return (
        <Container>
            <Row>
                <Col>
                    <Clock/>
                    <GoogleApis/>
                </Col>
                <Col>
                    <Weather/>
                </Col>
            </Row>
        </Container>);
    }
}

export default App;
