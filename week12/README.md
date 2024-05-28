# Running the script
Ensure that ds-server, ds-client, and your client are in this directory.
Run `ds_test.py`, passing the command to run your client as a string and -n if your client uses newline-terminated messages (i.e. Java clients).
You can specify a directory of config files with -c (`TestConfigs` is used by default if you omit -c).
You will also need to specify your port number with `-p` (use a unique port number, like 5 followed by the last 4 digits of your ID).
For example:
```
python3 ./ds_test.py "java MyClient" -n -p 50000 -c TestConfigs
```

To view the usage message that explains all options, use `-h`.
For example:
```
python3 ./ds_test.py -h
```

To speed up testing, results from the reference client using the configs in TestConfigs are read from `results/ref_results.json`.
However, if you want to try different configs you can make the script run them with ds-client and store the results in a new file.
The script will run ds-client when `--process_reference_client` is provided, and you can specify a new reference results file with `-r`.
For example (with a new set of configs stored in the NewConfigs directory):
```
python3 ./ds_test.py "java MyClient" -n -p 50000 -c NewConfigs --process_reference_client -r results/new_ref_results.json
```

For students submitting a custom algorithm, you can use `-i` which will use an extra set of configs (identified by their filename ending in `.ext.xml`).
These extra configs are marked differently, instead of just matching the performance of ds-client, you need to outperform ds-client.
By default, the metric used for comparisons is average turnaround time.
However, you can use total cost as the compared metric with `-o co`.
Resource utilisation is not an available choice as it is not a useful metric on its own.
For example:
```
python3 ./ds_test.py "java MyClient" -n -p 50000 -i -o co
```

If you made a mistake when running the script (e.g. misspelled your client name or forgot `-n`), then kill the script with CTRL+C and try again.

# Output explanation
When the script is finished, it will print out three tables (one for each metric).
The leftmost column lists the configuration files.
The middle column lists ds-client's results for each respective config.
The rightmost column lists your client's results for each respective config.
After the config rows, there is a row of averages.

The colour of values in the "Yours" column indicates your client's performance in comparison to ds-client.
Green indicates your client matched or exceeded ds-client's performance.
Red indicates your client performed worse than ds-client.

The final results are shown after the tables.
"Handshake" indicates whether your client has successfully completed the handshake with ds-server (and gracefully terminated with a `QUIT` message).
"Scheduling" indicates how well your client performed for the standard set of configs (maximum of 3, deduct 1 mark for every under-performing config).
"Innovation" indicates how well your client performed with the extra set of configs (1 mark for outperforming ds-client in at least 4 extra configs, 2 marks for outperforming with all extra configs).
