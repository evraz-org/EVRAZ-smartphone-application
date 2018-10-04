# evraz-wallet

1. `git clone --recursive https://bitbucket.org/zarincheg/evraz-wallet`
2. If submodule not init `git submodule add https://github.com/bituniverse/graphenej.git graphenej` 
3. Create google-services.json
  * Go to [this link](https://developers.google.com/mobile/add?platform=android&cntapi=signin&cnturl=https:%2F%2Fdevelopers.google.com%2Fidentity%2Fsign-in%2Fandroid%2Fsign-in%3Fconfigured%3Dtrue&cntlbl=Continue%20Adding%20Sign-In)
  * Configure
        App name: Evraz wallet
        Android package name: com.bitshares.bitshareswallet
  * Select analytics tab and select default or new project
  * Download file and put in app/
4. Build and run


* For use google signIn in prod, we create another google-services.json file in owner account with actual Android Signing Certificate SHA-1# evraz-wallet

## License

The package is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).
