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


import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {withStyles} from 'material-ui/styles';
import '../App.css';
import PrList from './marketing-window/PrList';
import MenuAppBar from './marketing-window/HeaderAppBar'
import axios from "axios/index";
import { LinearProgress } from 'material-ui/Progress';
import FeatureModal from "./marketing-window/FeatureModal.js";
import {getServer} from "../resources/util";


const styles = {
    blocks: {
        display: 'inline',
        float: 'left',
        padding: 10
    }


};


class MarketingWindow extends Component {

    modalOpen = (milestoneData) => {
        this.setState({
            milestoneData: milestoneData,
            openModal: true,
            openFeatureModal: false
        });
    };

    featureModalOpen = (product, version) => {
        this.setState({
            openFeatureModal: true,
            openModal: false,
            product: product,
            version: version
        })
    };

    constructor(props) {
        super(props);
        this.state = {
            prList: [],
            milestoneData: {},
            openModal: false,
            loadIssue: false,
            openFeatureModal: false,
            product: "",
            version: "",
            productName: "",
            versionName: "",
        };

        this.setPrTable = this.setPrTable.bind(this);
        this.modalOpen = this.modalOpen.bind(this);
        this.featureModalOpen = this.featureModalOpen.bind(this);
        this.setProductAndVersionNames = this.setProductAndVersionNames.bind(this);
    }

    setProductAndVersionNames(productName, versionName) {
        this.setState({
            productName: productName,
            versionName: versionName
        })
    }

    setPrTable(versionId, startDate, endDate) {
        if (versionId !== null) {
            let productObject = {};
            productObject["versionId"] = versionId;
            productObject["startDate"] = startDate;
            productObject["endDate"] = endDate;

            if (versionId !== '' && startDate !== '' && endDate !== '') {
                this.setState({
                    loadIssue: true,
                    openModal: false,
                    prList: [],
                    openFeatureModal: false
                }, () => (
                    axios.post(getServer() + '/features',
                        productObject
                    ).then(
                        (response) => {
                            console.log(response.data);
                            this.setState(
                                {
                                    prList: response.data,
                                    loadIssue: false,
                                    openModal: false,
                                }
                            );
                        }
                    )
                ));
            } else {
                this.setState({
                    openModal: false,
                    prList: [],
                    openFeatureModal: false
                })
            }
        }
    }


    render() {
        const {classes} = this.props;
        return (

            <div>
                <MenuAppBar
                    productUpdate={this.setProduct}
                    setprtable={this.setPrTable}
                    setNames={this.setProductAndVersionNames}
                    featureModal={this.featureModalOpen}
                />
                <div style={{height: 5}}>
                    {this.state.loadIssue && <LinearProgress/>}
                </div>

                <div className={classes.blocks}>
                    <PrList
                        prList={this.state.prList}
                        modalLauch={this.modalOpen}
                    />
                </div>

                <div>
                    <FeatureModal
                        open={this.state.openFeatureModal}
                        versionData={{
                            product: this.state.productName,
                            version: this.state.versionName
                        }}
                        prList={this.state.prList}
                    />
                </div>
            </div>


        );
    }
}

MarketingWindow.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(MarketingWindow);
