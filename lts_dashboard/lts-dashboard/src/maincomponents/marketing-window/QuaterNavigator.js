/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import {withStyles} from 'material-ui/styles';
import Input, {InputLabel} from 'material-ui/Input';
import {MenuItem} from 'material-ui/Menu';
import {FormControl} from 'material-ui/Form';
import Select from 'material-ui/Select';
import axios from "axios/index";
import { CircularProgress } from 'material-ui/Progress';
import {getServer} from "../../resources/util";

const styles = theme => ({
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    formControl: {
        margin: theme.spacing.unit,
        width: `100%`,
    },
    selectEmpty: {
        marginTop: theme.spacing.unit * 2,
    },
    progress: {
        margin: `0 ${theme.spacing.unit * 2}px`,
        paddingTop: 10
    },
});

class QuarterNavigator extends React.Component {

    handleChange = event => {
        this.setState({[event.target.name]: event.target.value},
            () => {
                let id = event.target.value;
                let obj = this.getObject(id);
                this.props.setQuarter(obj["startDate"],obj["endDate"]);
            });


    };

    constructor(props) {
        super(props);
        this.state = {
            quarter:'',
            quarterList: [],
            issueLoading: false,
        };
        this.fetchQuarters();
    }


    fetchQuarters() {
        axios.get(getServer()+'/lts/release/quarters').then(
            (response) => {
                let datat = response.data.sort(this.compareIds).reverse();
                this.setState(
                    {
                        quarterList: datat,
                        issueLoading: false,
                        quarter: datat[0].id
                    }
                );
                this.props.setQuarter(datat[0]["startDate"],datat[0]["endDate"])
            }
        )
    }


    compareIds(a,b){
        return a - b;
    }


    getObject(id){
        let quarterObject = null;
        this.state.quarterList.forEach(function (object) {
            if(object.id==id){
                quarterObject = object
            }
        });
        return quarterObject;
    }



    render() {
        const {classes} = this.props;

        return (
            <div>
                <form className={classes.container} autoComplete="off">
                    <FormControl className={classes.formControl}>
                        <InputLabel htmlFor="quarter-simple">Quarter</InputLabel>
                        <Select
                            value={this.state.quarter}
                            onChange={this.handleChange}
                            name="quarter"
                        >
                            {
                                this.state.quarterList.map((quarterData, index) => (
                                    <MenuItem key={index} value={quarterData.id}>{"Quarter "+(quarterData["quarter"]+1)+" - "+quarterData["year"]}</MenuItem>
                                ))
                            }
                        </Select>
                    </FormControl>
                    {this.state.issueLoading && <CircularProgress className={classes.progress} />}
                </form>

            </div>
        );
    }
}

QuarterNavigator.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(QuarterNavigator);