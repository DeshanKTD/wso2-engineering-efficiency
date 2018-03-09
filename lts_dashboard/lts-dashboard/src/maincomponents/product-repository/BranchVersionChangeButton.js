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
import Button from "material-ui/Button";


class BranchVersionChangeButton extends Component{
    constructor(props){
        super(props);
        this.changeBranchVersionOpen = this.changeBranchVersionOpen.bind(this);
        let buttonLabel = "Add Version";
        if(this.props.change){
            buttonLabel = "Change"
        }

        this.state = {
            buttonLabel : buttonLabel
        }
    }


    changeBranchVersionOpen(){
        let data = {
            change : this.props.change,
            versionId: this.props.versionId,
            branchId: this.props.branchId,
            branchName: this.props.branchName
        };
        this.props.openBranchChangeWindow(data);
    }



    render(){
        return (
            <Button color="secondary" onClick={this.changeBranchVersionOpen}>{this.state.buttonLabel}</Button>
        );
    }
}

export default BranchVersionChangeButton;