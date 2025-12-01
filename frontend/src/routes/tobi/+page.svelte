<script lang="ts">
	import Button from "$lib/components/Button/Button.svelte";
	import Divider from "$lib/components/Divider/Divider.svelte";
	import Table from "$lib/components/Table/Table.svelte";

  import Chip from "$lib/components/Chip/Chip.svelte";
	import Input from "$lib/components/Input/Input.svelte";
	import DatePicker from "$components/DatePicker/DatePicker.svelte";

  let counter = $state(0);

  interface User {
    id: number;
    name: string;
    age: number;
    active: boolean;
  }

  const users: User[] = [
    { id: 1, name: "Tobi", age: 23, active: true },
    { id: 2, name: "Jose", age: 22, active: false },
    { id: 3, name: "Tobi", age: 23, active: true },
    { id: 4, name: "Jose", age: 22, active: false },
  ];

  const columns = [
    { id: "name", label: "User" },
    { id: "age", label: "Age" },
    {
      id: "active",
      label: "Status",

      render: (row: User) =>
        row.active
          ? { component: Chip, props: { variant: "success", label: "Active" } }
          : { component: Chip, props: { variant: "destructive", label: "Inactive" } }
    }
  ];

  let selectedDate: Date | null = $state(null);

  const onSelectDate = (date: Date | null) => {
    console.log("Selected date:", date);
  };
</script>

<h1>Welcome to SvelteKit</h1>
<p>Counter: {counter}</p>

<Button variant="primary" onclick={() => counter++}>Increment</Button>
<Button variant="destructive" onclick={() => counter = 0}>Reset</Button>

<Divider size="md" />

<DatePicker selectedDate={selectedDate} onSelectDate={onSelectDate} />
