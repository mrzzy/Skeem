//
//  SKMSetting.swift
//  Skeem
//
//  Created by Zhu Zhan Yan on 29/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public enum SKMSetting:String
{
    //UI Settings
    case ui_shake =  "pvr_set_ui_shake" /* NSNumber<Bool> - Whether shake to refresh is enabled*/
}

public class SKMConfig:NSObject
{
    //Data
    var cfg:[String:NSCoding]

    //Init
    /* 
     * init(cfg:[String:NSCoding]
     * [Argument]
     * [String:NSCoding] - Inital Configuration data
    */
    init(cfg:[String:NSCoding])
    {
        self.cfg = cfg
    }

    //Data Methods
    /*
     * public func retrieveSetting(set:SKMSetting) -> NSCoding
     * - Retrieve value for setting specfied by set
     * 
    */
    public func retrieveSetting(set:SKMSetting) -> NSCoding
    {
        return self.cfg[set.rawValue]!
    }

    /*
     * public func commitSetting(set:SKMSetting,val:NSCoding)
     * - Save value for setting specifed by set
    */
    public func commitSetting(set:SKMSetting,val:NSCoding)
    {
        self.cfg[set.rawValue] = val
    }
}
