import React, { Component } from 'react';
//import { google } from 'googleapis';
import { Button } from 'reactstrap';
import ApiCalendar from 'react-google-calendar-api';

let client_id = '647610211813-h0lvh4tvmmdc7ol0vedgs3bpslk6bj3e.apps.googleusercontent.com';
let client_secret = 'EgE26D9anqSgjEHEVu6Xy_eJ';
let redirect_uri0 = '';



class GoogleApis extends Component {

    componentDidMount() {
  //      const oauth2Client = new google.auth.OAuth2(client_id, client_secret, redirect_uri0);
  //      google.options({auth: oauth2Client});

  //      const authorizeUrl = oauth2Client.generateAuthUrl({access_type: 'offline', scope: 'https://www.googleapis.com/auth/calendar'});
    }

    handle(e) {
        ApiCalendar.handleAuthClick();
    }
    
    render() {
        return(<div>
            <Button onClick={(e) => this.handle(e)}/>
            <p>Hello</p>
        </div>);
    }

}

export default GoogleApis

